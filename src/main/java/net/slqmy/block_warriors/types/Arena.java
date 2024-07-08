package net.slqmy.block_warriors.types;

import com.google.common.collect.TreeMultimap;
import net.slqmy.block_warriors.BlockWarriors;
import net.slqmy.block_warriors.enums.GameState;
import net.slqmy.block_warriors.enums.KitType;
import net.slqmy.block_warriors.enums.Team;
import net.slqmy.block_warriors.games.avoid_the_rain.AvoidTheRainGame;
import net.slqmy.block_warriors.games.block_muncher.BlockMuncherGame;
import net.slqmy.block_warriors.games.block_muncher.kits.MoleKit;
import net.slqmy.block_warriors.games.block_muncher.kits.VoleKit;
import net.slqmy.block_warriors.games.cactus_castle.CactusCastleGame;
import net.slqmy.block_warriors.games.gladiators.GladiatorsGame;
import net.slqmy.block_warriors.games.gladiators.kits.MurmilloKit;
import net.slqmy.block_warriors.games.gladiators.kits.RetiariusKit;
import net.slqmy.block_warriors.games.king_of_the_hill.KingOfTheHillGame;
import net.slqmy.block_warriors.games.one_in_the_chamber.OneInTheChamberGame;
import net.slqmy.block_warriors.games.slime_shooter.SlimeShooterGame;
import net.slqmy.block_warriors.utility.ConfigurationUtility;
import net.slqmy.block_warriors.utility.types.SignLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.BlockFace;
import org.bukkit.block.HangingSign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class Arena {
	private final BlockWarriors plugin;

	private final int id;
	private final Location spawnLocation;
	private final SignLocation signLocation;
	private final List<UUID> players = new ArrayList<>();
	private final HashMap<UUID, Team> teams = new HashMap<>();
	private final HashMap<UUID, AbstractKit> kits = new HashMap<>();
	private final String gameName;
	private GameState state = GameState.WAITING;
	private AbstractGame game;
	private Countdown countdown;

	public Arena(@NotNull final BlockWarriors plugin, final int id, @NotNull final Location spawnLocation,
	             @NotNull final SignLocation signLocation, @NotNull final String gameName) {
		this.plugin = plugin;

		this.id = id;
		this.spawnLocation = spawnLocation;
		this.signLocation = signLocation;

		signLocation.getBlock().setType(Material.OAK_HANGING_SIGN);

		final HangingSign sign = (HangingSign) signLocation.getBlock().getState();
		final org.bukkit.block.data.type.HangingSign signData = (org.bukkit.block.data.type.HangingSign) sign
						.getBlockData();

		final BlockFace signFacingDirection = signLocation.getFacingDirection();

		signData.setRotation(signFacingDirection);
		sign.setBlockData(signData);
		sign.update();

		updateSign();

		this.gameName = gameName;

		// DON'T FORGET TO BREAK (or use enhanced switch statement) IN SWITCH STATEMENTS!!!

		switch (gameName) {
			case "block-muncher" -> this.game = new BlockMuncherGame(plugin, this);
			case "gladiators" -> this.game = new GladiatorsGame(plugin, this);
			case "one-in-the-chamber" -> this.game = new OneInTheChamberGame(plugin, this);
			case "slime-shooter" -> this.game = new SlimeShooterGame(plugin, this);
			case "avoid-the-rain" -> this.game = new AvoidTheRainGame(plugin, this);
			case "king-of-the-hill" -> this.game = new KingOfTheHillGame(plugin, this);
			case "cactus-castle" -> this.game = new CactusCastleGame(plugin, this);
		}

		this.countdown = new Countdown(plugin, this);
	}

	public void addPlayer(@NotNull final Player player) {
		players.add(player.getUniqueId());
		updateSign();

		final TreeMultimap<Integer, Team> count = TreeMultimap.create();

		for (final Team team : Team.values()) {
			count.put(getTeamPlayerCount(team), team);
		}

		final Team smallestTeam = (Team) count.values().toArray()[0];
		setTeam(player, smallestTeam);

		player.teleport(spawnLocation);
		player.getInventory().clear();

		final AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		assert maxHealth != null;

		player.setHealth(maxHealth.getValue());

		player.sendMessage(
						ChatColor.DARK_GRAY + " \n| " + ChatColor.YELLOW + "» You have been added to the "
										+ smallestTeam.getDisplayName() + ChatColor.YELLOW + " team!\n" + ChatColor.DARK_GRAY + "| "
										+ ChatColor.GOLD + "» Use " + ChatColor.UNDERLINE + "/arena teams" + ChatColor.GOLD
										+ " to select your team!\n" + ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "» Use " + ChatColor.UNDERLINE
										+ "/arena kits" + ChatColor.GOLD + " to select your kit!\n ");

		if (state == GameState.WAITING && players.size() >= ConfigurationUtility.getMinPlayers()) {
			countdown.start();
		}
	}

	public void removePlayer(@NotNull final Player player) {
		player.teleport(ConfigurationUtility.getLobbySpawn());
		player.getInventory().clear();
		player.sendTitle("", "", 0, 0, 0);

		for (final PotionEffectType effectType : PotionEffectType.values()) {
			player.removePotionEffect(
							effectType
			);
		}

		removeKit(player);
		removeTeam(player);

		players.remove(player.getUniqueId());
		updateSign();

		if (players.size() < ConfigurationUtility.getMinPlayers()) {
			if (state == GameState.COUNTDOWN) {
				sendMessage(ChatColor.RED + "There are not enough players! Countdown cancelled.");
				sendTitle(ChatColor.RED + "Countdown cancelled!");

				reset(false);
			} else if (state == GameState.PLAYING) {
				sendMessage(ChatColor.RED + "The game has ended because too many players have left.");

				reset(true);
			}
		}
	}

	public void start() {
		game.start();
	}

	public void reset(final boolean kickPlayers) {
		if (kickPlayers) {
			final Location location = ConfigurationUtility.getLobbySpawn();

			for (final UUID uuid : players) {
				final Player player = Bukkit.getPlayer(uuid);
				assert player != null;

				player.getInventory().clear();
				player.teleport(location);

				for (final PotionEffectType effectType : PotionEffectType.values()) {
					player.removePotionEffect(
									effectType
					);
				}

				removeKit(player);
				removeTeam(player);
			}

			players.clear();
			teams.clear();
		}

		state = GameState.WAITING;

		countdown.cancel();

		game.end();

		countdown = new Countdown(plugin, this);

		// DON'T FORGET TO BREAK (or use enhanced switch statement) IN SWITCH STATEMENTS!!!

		kits.clear();

		switch (gameName) {
			case "block-muncher" -> this.game = new BlockMuncherGame(plugin, this);
			case "gladiators" -> this.game = new GladiatorsGame(plugin, this);
			case "one-in-the-chamber" -> this.game = new OneInTheChamberGame(plugin, this);
			case "slime-shooter" -> this.game = new SlimeShooterGame(plugin, this);
			case "avoid-the-rain" -> this.game = new AvoidTheRainGame(plugin, this);
			case "king-of-the-hill" -> this.game = new KingOfTheHillGame(plugin, this);
			case "cactus-caste" -> this.game = new CactusCastleGame(plugin, this);
		}

		updateSign();
	}

	public void sendMessage(@NotNull final String message) {
		for (final UUID uuid : players) {
			final Player player = Bukkit.getPlayer(uuid);
			assert player != null;

			player.sendMessage(message);
		}
	}

	public void sendTitle(@NotNull final String title) {
		for (final UUID uuid : players) {
			final Player player = Bukkit.getPlayer(uuid);
			assert player != null;

			player.sendTitle(title, "", 10, 10, 10);
		}
	}

	public void updateSign() {
		final HangingSign sign = (HangingSign) signLocation.getBlock().getState();

		final SignSide front = sign.getSide(Side.FRONT);

		final int playerCount = players.size();

		front.setLine(0, "==========");
		front.setLine(1,
						ChatColor.GOLD + "Arena " + ChatColor.YELLOW + id + ChatColor.DARK_GRAY + " ("
										+ (state == GameState.PLAYING || playerCount == 0 ? ChatColor.RED
										: playerCount < ConfigurationUtility.getMinPlayers() ? ChatColor.YELLOW : ChatColor.GREEN)
										+ playerCount + ChatColor.DARK_GRAY + ")");
		front.setLine(2, state.getDisplayName());
		front.setLine(3, "==========");

		sign.update();
	}

	public int getID() {
		return id;
	}

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public SignLocation getSignLocation() {
		return signLocation;
	}

	public GameState getState() {
		return state;
	}

	public void setState(@NotNull final GameState state) {
		this.state = state;
		updateSign();
	}

	public List<UUID> getPlayers() {
		return players;
	}

	public Team getTeam(@NotNull final Player player) {
		return teams.get(player.getUniqueId());
	}

	public Team getTeam(@NotNull final UUID uuid) {
		return teams.get(uuid);
	}

	public int getTeamPlayerCount(@NotNull final Team team) {
		int count = 0;

		for (final Team currentTeam : teams.values()) {
			if (currentTeam == team) {
				count++;
			}
		}

		return count;
	}

	public HashMap<UUID, AbstractKit> getKits() {
		return kits;
	}

	public @Nullable KitType getKit(@NotNull final Player player) {
		final UUID uuid = player.getUniqueId();

		return kits.containsKey(uuid) ? kits.get(uuid).getKitType() : null;
	}

	public AbstractGame getGame() {
		return game;
	}

	public String getGameName() {
		return gameName;
	}

	public void setTeam(@NotNull final Player player, @NotNull final Team team) {
		removeTeam(player);
		teams.put(player.getUniqueId(), team);
	}

	public void removeTeam(@NotNull final Player player) {
		teams.remove(player.getUniqueId());
	}

	public void setKit(@NotNull final Player player, @NotNull final KitType kitType) {
		removeKit(player);

		final UUID uuid = player.getUniqueId();

		final AbstractKit kit;

		if (kitType == KitType.MOLE) {
			kit = new MoleKit(plugin, player);
		} else if (kitType == KitType.VOLE) {
			kit = new VoleKit(plugin, player);
		} else if (kitType == KitType.RETIARIUS) {
			kit = new RetiariusKit(plugin, player);
		} else {
			kit = new MurmilloKit(plugin, player);
		}

		kits.put(uuid, kit);
	}

	public void removeKit(@NotNull final Player player) {
		final AbstractKit playerKit = kits.get(player.getUniqueId());

		if (playerKit != null) {
			playerKit.remove();
			kits.remove(player.getUniqueId());
		}
	}
}
