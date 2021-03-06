/* Decompiler 5204ms, total 6882ms, lines 8346 */
package zombie.network;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.AmbientSoundManager;
import zombie.AmbientStreamManager;
import zombie.DebugFileWatcher;
import zombie.GameProfiler;
import zombie.GameSounds;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.MapCollisionData;
import zombie.PersistentOutfits;
import zombie.SandboxOptions;
import zombie.SharedDescriptors;
import zombie.SoundManager;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZomboidFileSystem;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.Lua.LuaManager.GlobalObject;
import zombie.SharedDescriptors.Descriptor;
import zombie.ai.states.ZombieGetUpState;
import zombie.ai.states.ZombieOnGroundState;
import zombie.asset.AssetManagers;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.characters.Faction;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorFactory;
import zombie.characters.AttachedItems.AttachedItem;
import zombie.characters.AttachedItems.AttachedItems;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.IsoZombie.ZombieSound;
import zombie.characters.WornItems.WornItem;
import zombie.characters.WornItems.WornItems;
import zombie.characters.skills.CustomPerks;
import zombie.characters.skills.PerkFactory;
import zombie.characters.skills.PerkFactory.Perk;
import zombie.characters.skills.PerkFactory.Perks;
import zombie.commands.CommandBase;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Languages;
import zombie.core.PerformanceSettings;
import zombie.core.ProxyPrintStream;
import zombie.core.Rand;
import zombie.core.ThreadGroups;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.core.network.ByteBufferReader;
import zombie.core.network.ByteBufferWriter;
import zombie.core.physics.Bullet;
import zombie.core.raknet.RakNetPeerInterface;
import zombie.core.raknet.RakVoice;
import zombie.core.raknet.UdpConnection;
import zombie.core.raknet.UdpEngine;
import zombie.core.raknet.UdpConnection.ChecksumState;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.advancedanimation.AnimNodeAssetManager;
import zombie.core.skinnedmodel.model.AiSceneAsset;
import zombie.core.skinnedmodel.model.AiSceneAssetManager;
import zombie.core.skinnedmodel.model.AnimationAsset;
import zombie.core.skinnedmodel.model.AnimationAssetManager;
import zombie.core.skinnedmodel.model.MeshAssetManager;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.ModelAssetManager;
import zombie.core.skinnedmodel.model.ModelMesh;
import zombie.core.skinnedmodel.model.jassimp.JAssImpImporter;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.core.skinnedmodel.population.ClothingDecals;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.population.ClothingItemAssetManager;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.stash.StashSystem;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureAssetManager;
import zombie.core.textures.TextureID;
import zombie.core.textures.TextureIDAssetManager;
import zombie.core.znet.PortMapper;
import zombie.core.znet.SteamGameServer;
import zombie.core.znet.SteamUtils;
import zombie.core.znet.SteamWorkshop;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.debug.LogSeverity;
import zombie.erosion.ErosionMain;
import zombie.gameStates.ChooseGameInfo;
import zombie.gameStates.IngameState;
import zombie.gameStates.ChooseGameInfo.Mod;
import zombie.globalObjects.SGlobalObjectNetwork;
import zombie.globalObjects.SGlobalObjects;
import zombie.inventory.CompressIdenticalItems;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemPickerJava;
import zombie.inventory.RecipeManager;
import zombie.inventory.types.AlarmClock;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Radio;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.ObjectsSyncRequests;
import zombie.iso.RoomDef;
import zombie.iso.SpawnPoints;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import zombie.iso.IsoMetaGrid.Zone;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.areas.SafeHouse;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.objects.BSFurnace;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoFire;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoMannequin;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTrap;
import zombie.iso.objects.IsoWaveSignal;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.RainManager;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.weather.ClimateManager;
import zombie.network.ServerWorldDatabase.LogonResult;
import zombie.network.Userlog.UserlogType;
import zombie.network.chat.ChatServer;
import zombie.network.packets.DeadBodyPacket;
import zombie.network.packets.EventUpdatePacket;
import zombie.network.packets.HitPacket;
import zombie.network.packets.PlayerPacket;
import zombie.network.packets.ZombieUpdateInfoPacket;
import zombie.network.packets.EventUpdatePacket.l_receive;
import zombie.network.packets.HitPacket.HitVehicle;
import zombie.popman.ZombiePopulationManager;
import zombie.radio.ZomboidRadio;
import zombie.radio.devices.DeviceData;
import zombie.sandbox.CustomSandboxOptions;
import zombie.savefile.ServerPlayerDB;
import zombie.scripting.ScriptManager;
import zombie.util.PZSQLUtils;
import zombie.util.PublicServerUtil;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.Clipper;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleManager;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.VehiclesDB2;
import zombie.world.WorldDictionary;
import zombie.world.moddata.GlobalModData;

public class GameServer {
   public static final int MAX_PLAYERS = 512;
   public static final int TimeLimitForProcessPackets = 70;
   public static final int FPS = 10;
   private static final long[] packetCounts = new long[256];
   private static final HashMap<String, zombie.network.GameServer.CCFilter> ccFilters = new HashMap();
   public static int test = 432432;
   public static int DEFAULT_PORT = 16261;
   public static String IPCommandline = null;
   public static int PortCommandline = -1;
   public static int SteamPortCommandline1 = -1;
   public static int SteamPortCommandline2 = -1;
   public static Boolean SteamVACCommandline;
   public static boolean GUICommandline;
   public static boolean bServer = false;
   public static boolean bCoop = false;
   public static boolean bDebug = false;
   public static UdpEngine udpEngine;
   public static final HashMap<Integer, Long> IDToAddressMap = new HashMap();
   public static final HashMap<Integer, IsoPlayer> IDToPlayerMap = new HashMap();
   public static final ArrayList<IsoPlayer> Players = new ArrayList();
   public static float timeSinceKeepAlive = 0.0F;
   public static int MaxTicksSinceKeepAliveBeforeStall = 60;
   public static final HashMap<IsoPlayer, IsoDeadBody> PlayerToBody = new HashMap();
   public static final HashSet<UdpConnection> DebugPlayer = new HashSet();
   public static int ResetID = 0;
   public static final ArrayList<String> ServerMods = new ArrayList();
   public static final ArrayList<Long> WorkshopItems = new ArrayList();
   public static String[] WorkshopInstallFolders;
   public static long[] WorkshopTimeStamps;
   public static String ServerName = "servertest";
   public static final DiscordBot discordBot;
   public static String checksum;
   public static String GameMap;
   public static boolean bFastForward;
   public static boolean UseTCPForMapDownloads;
   public static final HashMap<String, Integer> transactionIDMap;
   public static final ObjectsSyncRequests worldObjectsServerSyncReq;
   public static String ip;
   static int count;
   private static final UdpConnection[] SlotToConnection;
   private static final HashMap<IsoPlayer, Long> PlayerToAddressMap;
   private static final ArrayList<Integer> alreadyRemoved;
   private static int SendZombies;
   private static boolean bDone;
   private static boolean launched;
   private static final ArrayList<String> consoleCommands;
   private static final HashMap<Integer, IZomboidPacket> MainLoopPlayerUpdate;
   private static final ArrayList<IZomboidPacket> MainLoopNetDataHighPrioritet;
   private static final ArrayList<IZomboidPacket> MainLoopNetData;
   private static final ArrayList<IZomboidPacket> MainLoopNetData2;
   private static final HashMap<Integer, Vector2> playerToCoordsMap;
   private static final HashMap<Integer, Integer> playerMovedToFastMap;
   private static final ByteBuffer large_file_bb;
   private static long previousSave;
   private String poisonousBerry = null;
   private String poisonousMushroom = null;
   private String difficulty = "Hardcore";
   private static int droppedPackets;
   private static int countOfDroppedPackets;
   private static int countOfDroppedConnections;

   public static void PauseAllClients() {
      String var0 = "[SERVERMSG] Server saving...Please wait";

      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);
         ByteBufferWriter var3 = var2.startPacket();
         PacketTypes.doPacket((short)158, var3);
         var3.putUTF(var0);
         var2.endPacketImmediate();
      }

   }

   public static void UnPauseAllClients() {
      String var0 = "[SERVERMSG] Server saved game...enjoy :)";

      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);
         ByteBufferWriter var3 = var2.startPacket();
         PacketTypes.doPacket((short)159, var3);
         var3.putUTF(var0);
         var2.endPacketImmediate();
      }

   }

   private static String parseIPFromCommandline(String[] var0, int var1, String var2) {
      if (var1 == var0.length - 1) {
         DebugLog.log("expected argument after \"" + var2 + "\"");
         System.exit(0);
      } else if (var0[var1 + 1].trim().isEmpty()) {
         DebugLog.log("empty argument given to \"\" + option + \"\"");
         System.exit(0);
      } else {
         String[] var3 = var0[var1 + 1].trim().split("\\.");
         if (var3.length == 4) {
            for(int var4 = 0; var4 < 4; ++var4) {
               try {
                  int var5 = Integer.parseInt(var3[var4]);
                  if (var5 < 0 || var5 > 255) {
                     DebugLog.log("expected IP address after \"" + var2 + "\", got \"" + var0[var1 + 1] + "\"");
                     System.exit(0);
                  }
               } catch (NumberFormatException var6) {
                  DebugLog.log("expected IP address after \"" + var2 + "\", got \"" + var0[var1 + 1] + "\"");
                  System.exit(0);
               }
            }
         } else {
            DebugLog.log("expected IP address after \"" + var2 + "\", got \"" + var0[var1 + 1] + "\"");
            System.exit(0);
         }
      }

      return var0[var1 + 1];
   }

   private static int parsePortFromCommandline(String[] var0, int var1, String var2) {
      if (var1 == var0.length - 1) {
         DebugLog.log("expected argument after \"" + var2 + "\"");
         System.exit(0);
      } else if (var0[var1 + 1].trim().isEmpty()) {
         DebugLog.log("empty argument given to \"" + var2 + "\"");
         System.exit(0);
      } else {
         try {
            return Integer.parseInt(var0[var1 + 1].trim());
         } catch (NumberFormatException var4) {
            DebugLog.log("expected an integer after \"" + var2 + "\"");
            System.exit(0);
         }
      }

      return -1;
   }

   private static boolean parseBooleanFromCommandline(String[] var0, int var1, String var2) {
      if (var1 == var0.length - 1) {
         DebugLog.log("expected argument after \"" + var2 + "\"");
         System.exit(0);
      } else if (var0[var1 + 1].trim().isEmpty()) {
         DebugLog.log("empty argument given to \"" + var2 + "\"");
         System.exit(0);
      } else {
         String var3 = var0[var1 + 1].trim();
         if ("true".equalsIgnoreCase(var3)) {
            return true;
         }

         if ("false".equalsIgnoreCase(var3)) {
            return false;
         }

         DebugLog.log("expected true or false after \"" + var2 + "\"");
         System.exit(0);
      }

      return false;
   }

   public static void setupCoop() throws FileNotFoundException {
      CoopSlave.init();
   }

   public static void main(String[] var0) {
      bServer = true;

      int var1;
      for(var1 = 0; var1 < var0.length; ++var1) {
         if (var0[var1] != null) {
            if (var0[var1].startsWith("-cachedir=")) {
               ZomboidFileSystem.instance.setCacheDir(var0[var1].replace("-cachedir=", "").trim());
            } else if (var0[var1].equals("-coop")) {
               bCoop = true;
            }
         }
      }

      String var10000;
      String var89;
      if (bCoop) {
         try {
            CoopSlave.initStreams();
         } catch (FileNotFoundException var84) {
            var84.printStackTrace();
         }
      } else {
         try {
            var10000 = ZomboidFileSystem.instance.getCacheDir();
            var89 = var10000 + File.separator + "server-console.txt";
            FileOutputStream var2 = new FileOutputStream(var89);
            PrintStream var3 = new PrintStream(var2, true);
            System.setOut(new ProxyPrintStream(System.out, var3));
            System.setErr(new ProxyPrintStream(System.err, var3));
         } catch (FileNotFoundException var83) {
            var83.printStackTrace();
         }
      }

      DebugLog.init();
      LoggerManager.init();
      DebugLog.log("cachedir set to \"" + ZomboidFileSystem.instance.getCacheDir() + "\"");
      if (bCoop) {
         try {
            setupCoop();
            CoopSlave.status(Translator.getText("UI_ServerStatus_Initialising"));
         } catch (FileNotFoundException var82) {
            var82.printStackTrace();
            SteamUtils.shutdown();
            System.exit(37);
            return;
         }
      }

      PZSQLUtils.init();
      Clipper.init();
      Bullet.init();
      Rand.init();
      DebugLog.setLogEnabled(DebugType.General, true);
      DebugLog.setLogEnabled(DebugType.Network, true);
      DebugLog.setLogEnabled(DebugType.Lua, true);
      if (System.getProperty("debug") != null) {
         bDebug = true;
         Core.bDebug = true;
      }

      DebugLog.General.println("versionNumber=%s demo=%s", Core.getInstance().getVersionNumber(), false);

      int var4;
      String var5;
      for(var1 = 0; var1 < var0.length; ++var1) {
         if (var0[var1] != null) {
            if (var0[var1].startsWith("-debuglog=")) {
               String[] var90 = var0[var1].replace("-debuglog=", "").split(",");
               int var92 = var90.length;

               for(var4 = 0; var4 < var92; ++var4) {
                  var5 = var90[var4];

                  try {
                     DebugLog.setLogEnabled(DebugType.valueOf(var5), true);
                  } catch (IllegalArgumentException var81) {
                  }
               }
            } else if (var0[var1].equals("-adminusername")) {
               if (var1 == var0.length - 1) {
                  DebugLog.log("expected argument after \"-adminusername\"");
                  System.exit(0);
               } else if (!ServerWorldDatabase.isValidUserName(var0[var1 + 1].trim())) {
                  DebugLog.log("invalid username given to \"-adminusername\"");
                  System.exit(0);
               } else {
                  ServerWorldDatabase.instance.CommandLineAdminUsername = var0[var1 + 1].trim();
                  ++var1;
               }
            } else if (var0[var1].equals("-adminpassword")) {
               if (var1 == var0.length - 1) {
                  DebugLog.log("expected argument after \"-adminpassword\"");
                  System.exit(0);
               } else if (var0[var1 + 1].trim().isEmpty()) {
                  DebugLog.log("empty argument given to \"-adminpassword\"");
                  System.exit(0);
               } else {
                  ServerWorldDatabase.instance.CommandLineAdminPassword = var0[var1 + 1].trim();
                  ++var1;
               }
            } else if (!var0[var1].startsWith("-cachedir=")) {
               if (var0[var1].equals("-ip")) {
                  IPCommandline = parseIPFromCommandline(var0, var1, "-ip");
                  ++var1;
               } else if (var0[var1].equals("-gui")) {
                  GUICommandline = true;
               } else if (var0[var1].equals("-nosteam")) {
                  System.setProperty("zomboid.steam", "0");
               } else if (var0[var1].equals("-port")) {
                  PortCommandline = parsePortFromCommandline(var0, var1, "-port");
                  ++var1;
               } else if (var0[var1].equals("-steamport1")) {
                  SteamPortCommandline1 = parsePortFromCommandline(var0, var1, "-steamport1");
                  ++var1;
               } else if (var0[var1].equals("-steamport2")) {
                  SteamPortCommandline2 = parsePortFromCommandline(var0, var1, "-steamport2");
                  ++var1;
               } else if (var0[var1].equals("-steamvac")) {
                  SteamVACCommandline = parseBooleanFromCommandline(var0, var1, "-steamvac");
                  ++var1;
               } else if (var0[var1].equals("-servername")) {
                  if (var1 == var0.length - 1) {
                     DebugLog.log("expected argument after \"-servername\"");
                     System.exit(0);
                  } else if (var0[var1 + 1].trim().isEmpty()) {
                     DebugLog.log("empty argument given to \"-servername\"");
                     System.exit(0);
                  } else {
                     ServerName = var0[var1 + 1].trim();
                     ++var1;
                  }
               } else if (var0[var1].equals("-coop")) {
                  ServerWorldDatabase.instance.doAdmin = false;
               } else {
                  DebugLog.log("unknown option \"" + var0[var1] + "\"");
               }
            }
         }
      }

      DebugLog.log("server name is \"" + ServerName + "\"");
      var89 = isWorldVersionUnsupported();
      if (var89 != null) {
         DebugLog.log(var89);
         CoopSlave.status(var89);
      } else {
         SteamUtils.init();
         RakNetPeerInterface.init();
         ZombiePopulationManager.init();
         ServerOptions.instance.init();
         initClientCommandFilter();
         if (PortCommandline != -1) {
            ServerOptions.instance.DefaultPort.setValue(PortCommandline);
         }

         if (SteamPortCommandline1 != -1) {
            ServerOptions.instance.SteamPort1.setValue(SteamPortCommandline1);
         }

         if (SteamPortCommandline2 != -1) {
            ServerOptions.instance.SteamPort2.setValue(SteamPortCommandline2);
         }

         if (SteamVACCommandline != null) {
            ServerOptions.instance.SteamVAC.setValue(SteamVACCommandline);
         }

         DEFAULT_PORT = ServerOptions.instance.DefaultPort.getValue();
         UseTCPForMapDownloads = ServerOptions.instance.UseTCPForMapDownloads.getValue();
         if (CoopSlave.instance != null) {
            ServerOptions.instance.ServerPlayerID.setValue("");
         }

         String var91;
         if (SteamUtils.isSteamModeEnabled()) {
            var91 = ServerOptions.instance.PublicName.getValue();
            if (var91 == null || var91.isEmpty()) {
               ServerOptions.instance.PublicName.setValue("My PZ Server");
            }
         }

         var91 = ServerOptions.instance.Map.getValue();
         if (var91 != null && !var91.trim().isEmpty()) {
            GameMap = var91.trim();
            if (GameMap.contains(";")) {
               String[] var93 = GameMap.split(";");
               var91 = var93[0];
            }

            Core.GameMap = var91.trim();
         }

         String var94 = ServerOptions.instance.Mods.getValue();
         int var6;
         String var8;
         if (var94 != null) {
            String[] var95 = var94.split(";");
            String[] var96 = var95;
            var6 = var95.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               var8 = var96[var7];
               if (!var8.trim().isEmpty()) {
                  ServerMods.add(var8.trim());
               }
            }
         }

         int var10;
         int var105;
         if (SteamUtils.isSteamModeEnabled()) {
            var4 = ServerOptions.instance.SteamPort1.getValue();
            int var97 = ServerOptions.instance.SteamPort2.getValue();
            var6 = ServerOptions.instance.SteamVAC.getValue() ? 3 : 2;
            if (!SteamGameServer.Init(IPCommandline, var4, var97, DEFAULT_PORT, var6, Core.getInstance().getSteamServerVersion())) {
               SteamUtils.shutdown();
               return;
            }

            SteamGameServer.SetProduct("zomboid");
            SteamGameServer.SetGameDescription("Project Zomboid");
            SteamGameServer.SetModDir("zomboid");
            SteamGameServer.SetDedicatedServer(true);
            SteamGameServer.SetMaxPlayerCount(ServerOptions.instance.MaxPlayers.getValue());
            SteamGameServer.SetServerName(ServerOptions.instance.PublicName.getValue());
            SteamGameServer.SetMapName(ServerOptions.instance.Map.getValue());
            if (ServerOptions.instance.Public.getValue()) {
               var10000 = ServerOptions.instance.Mods.getValue();
               SteamGameServer.SetGameTags(var10000 + (CoopSlave.instance != null ? ";hosted" : ""));
            } else {
               SteamGameServer.SetGameTags("hidden" + (CoopSlave.instance != null ? ";hosted" : ""));
            }

            SteamGameServer.SetKeyValue("description", ServerOptions.instance.PublicDescription.getValue());
            SteamGameServer.SetKeyValue("version", Core.getInstance().getVersionNumber());
            SteamGameServer.SetKeyValue("open", ServerOptions.instance.Open.getValue() ? "1" : "0");
            SteamGameServer.SetKeyValue("public", ServerOptions.instance.Public.getValue() ? "1" : "0");
            if (bDebug) {
               SteamGameServer.SetKeyValue("description", "0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789\n0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789\n0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789\n0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789\n0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789\n0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789\n");

               try {
                  byte[] var100 = new byte[]{-16, -96, -100, -114};
                  var8 = new String(var100, "UTF-8");
                  String var9 = "";

                  for(var10 = 0; var10 < 128; ++var10) {
                     var9 = var9 + var8;
                  }

                  SteamGameServer.SetKeyValue("test", var9);
               } catch (UnsupportedEncodingException var88) {
               }

               SteamGameServer.SetKeyValue("test2", "12345");
            }

            String var101 = ServerOptions.instance.WorkshopItems.getValue();
            if (var101 != null) {
               String[] var103 = var101.split(";");
               String[] var106 = var103;
               var10 = var103.length;

               for(int var11 = 0; var11 < var10; ++var11) {
                  String var12 = var106[var11];
                  var12 = var12.trim();
                  if (!var12.isEmpty() && SteamUtils.isValidSteamID(var12)) {
                     WorkshopItems.add(SteamUtils.convertStringToSteamID(var12));
                  }
               }
            }

            SteamWorkshop.init();
            SteamGameServer.LogOnAnonymous();
            SteamGameServer.EnableHeartBeats(true);
            DebugLog.log("Waiting for response from Steam servers");

            while(true) {
               SteamUtils.runLoop();
               var105 = SteamGameServer.GetSteamServersConnectState();
               if (var105 == SteamGameServer.STEAM_SERVERS_CONNECTED) {
                  if (!GameServerWorkshopItems.Install(WorkshopItems)) {
                     return;
                  }
                  break;
               }

               if (var105 == SteamGameServer.STEAM_SERVERS_CONNECTFAILURE) {
                  DebugLog.log("Failed to connect to Steam servers");
                  SteamUtils.shutdown();
                  return;
               }

               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var80) {
               }
            }
         }

         var4 = 0;

         try {
            ServerWorldDatabase.instance.create();
         } catch (ClassNotFoundException | SQLException var79) {
            var79.printStackTrace();
         }

         if (ServerOptions.instance.UPnP.getValue()) {
            DebugLog.log("Router detection/configuration starting.");
            DebugLog.log("If the server hangs here, set UPnP=false.");
            PortMapper.startup();
            if (PortMapper.discover()) {
               DebugLog.log("UPnP-enabled internet gateway found: " + PortMapper.getGatewayInfo());
               var5 = PortMapper.getExternalAddress();
               DebugLog.log("External IP address: " + var5);
               DebugLog.log("trying to setup port forwarding rules...");
               var6 = ServerOptions.instance.UPnPLeaseTime.getValue();
               boolean var102 = ServerOptions.instance.UPnPForce.getValue();
               if (PortMapper.addMapping(DEFAULT_PORT, DEFAULT_PORT, "PZ Server default port", "UDP", var6, var102)) {
                  DebugLog.log(DebugType.Network, "Default port has been mapped successfully");
               } else {
                  DebugLog.log(DebugType.Network, "Failed to map default port");
               }

               int var107;
               if (SteamUtils.isSteamModeEnabled()) {
                  var105 = ServerOptions.instance.SteamPort1.getValue();
                  if (PortMapper.addMapping(var105, var105, "PZ Server SteamPort1", "UDP", var6, var102)) {
                     DebugLog.log(DebugType.Network, "SteamPort1 has been mapped successfully");
                  } else {
                     DebugLog.log(DebugType.Network, "Failed to map SteamPort1");
                  }

                  var107 = ServerOptions.instance.SteamPort2.getValue();
                  if (PortMapper.addMapping(var107, var107, "PZ Server SteamPort2", "UDP", var6, var102)) {
                     DebugLog.log(DebugType.Network, "SteamPort2 has been mapped successfully");
                  } else {
                     DebugLog.log(DebugType.Network, "Failed to map SteamPort2");
                  }
               }

               if (UseTCPForMapDownloads) {
                  for(var105 = 1; var105 <= ServerOptions.instance.MaxPlayers.getValue(); ++var105) {
                     var107 = DEFAULT_PORT + var105;
                     if (PortMapper.addMapping(var107, var107, "PZ Server TCP Port " + var105, "TCP", var6, var102)) {
                        DebugLog.log(DebugType.Network, var107 + " has been mapped successfully");
                     } else {
                        DebugLog.log(DebugType.Network, "Failed to map TCP port " + var107);
                     }
                  }
               }
            } else {
               DebugLog.log(DebugType.Network, "No UPnP-enabled Internet gateway found, you must configure port forwarding on your gateway manually in order to make your server accessible from the Internet.");
            }
         }

         Core.GameMode = "Multiplayer";
         bDone = false;
         DebugLog.log(DebugType.Network, "Initialising Server Systems...");
         CoopSlave.status(Translator.getText("UI_ServerStatus_Init"));

         try {
            doMinimumInit();
         } catch (Exception var78) {
            DebugLog.General.printException(var78, "Exception Thrown", LogSeverity.Error);
            DebugLog.General.println("Server Terminated.");
         }

         LosUtil.init(100, 100);
         ChatServer.getInstance().init();
         DebugLog.log(DebugType.Network, "Loading world...");
         CoopSlave.status(Translator.getText("UI_ServerStatus_LoadingWorld"));

         try {
            ClimateManager.setInstance(new ClimateManager());
            IsoWorld.instance.init();
         } catch (Exception var77) {
            DebugLog.General.printException(var77, "Exception Thrown", LogSeverity.Error);
            DebugLog.General.println("Server Terminated.");
            CoopSlave.status(Translator.getText("UI_ServerStatus_Terminated"));
            return;
         }

         File var98 = ZomboidFileSystem.instance.getFileInCurrentSave("z_outfits.bin");
         if (!var98.exists()) {
            ServerOptions.instance.changeOption("ResetID", (new Integer(Rand.Next(100000000))).toString());
         }

         try {
            SpawnPoints.instance.initServer2();
         } catch (Exception var76) {
            var76.printStackTrace();
         }

         LuaEventManager.triggerEvent("OnGameTimeLoaded");
         SGlobalObjects.initSystems();
         SoundManager.instance = new SoundManager();
         AmbientStreamManager.instance = new AmbientSoundManager();
         AmbientStreamManager.instance.init();
         ServerMap.instance.LastSaved = System.currentTimeMillis();
         VehicleManager.instance = new VehicleManager();
         ServerPlayersVehicles.instance.init();
         DebugOptions.instance.init();
         GameProfiler.init();

         try {
            startServer();
         } catch (ConnectException var75) {
            var75.printStackTrace();
            SteamUtils.shutdown();
            return;
         }

         if (SteamUtils.isSteamModeEnabled()) {
            DebugLog.log("##########\nServer Steam ID " + SteamGameServer.GetSteamID() + "\n##########");
         }

         PerformanceSettings.setLockFPS(10);
         IngameState var99 = new IngameState();
         float var104 = 0.0F;
         float var108 = 0.0F;
         float[] var109 = new float[20];

         for(var10 = 0; var10 < 20; ++var10) {
            var109[var10] = (float)PerformanceSettings.getLockFPS();
         }

         boolean var110 = false;
         float var111 = (float)PerformanceSettings.getLockFPS();
         long var20 = 0L;
         int var22 = 0;
         long var23 = 0L;
         long var25 = 1000000000L / (long)PerformanceSettings.getLockFPS();
         if (!SteamUtils.isSteamModeEnabled()) {
            PublicServerUtil.init();
            PublicServerUtil.insertOrUpdate();
         }

         ServerLOS.init();
         int var27 = ServerOptions.instance.RCONPort.getValue();
         String var28 = ServerOptions.instance.RCONPassword.getValue();
         if (var27 != 0 && var28 != null && !var28.isEmpty()) {
            RCONServer.init(var27, var28);
         }

         GlobalObject.refreshAnimSets(true);

         while(!bDone) {
            try {
               MPStatistic.getInstance().Main.Start();
               ++IsoCamera.frameState.frameCount;
               zombie.network.GameServer.s_performance.frameStep.start();
               timeSinceKeepAlive += GameTime.getInstance().getMultiplier();
               long var112 = System.nanoTime();
               long var29 = System.currentTimeMillis();
               var25 = 1000000000L / (long)PerformanceSettings.getLockFPS();
               double var31 = ServerOptions.instance.ZombieUpdateDelta.getValue();
               ++SendZombies;
               if ((double)((float)SendZombies / var111) > var31) {
                  SendZombies = 0;
               }

               MPStatistic.getInstance().ServerMapPreupdate.Start();
               ServerMap.instance.preupdate();
               MPStatistic.getInstance().ServerMapPreupdate.End();
               long var33 = System.currentTimeMillis();
               synchronized(MainLoopNetDataHighPrioritet) {
                  MainLoopNetData2.clear();
                  MainLoopNetData2.addAll(MainLoopNetDataHighPrioritet);
                  MainLoopNetDataHighPrioritet.clear();
               }

               MPStatistic.getInstance().setPacketsLength((long)MainLoopNetData2.size());

               int var35;
               IZomboidPacket var36;
               for(var35 = 0; var35 < MainLoopNetData2.size(); ++var35) {
                  var36 = (IZomboidPacket)MainLoopNetData2.get(var35);
                  UdpConnection var37;
                  if (var36.isConnect()) {
                     var37 = ((zombie.network.GameServer.DelayedConnection)var36).connection;
                     LoggerManager.getLogger("user").write("added connection index=" + var37.index + " " + ((zombie.network.GameServer.DelayedConnection)var36).hostString);
                     udpEngine.connections.add(var37);
                  } else if (var36.isDisconnect()) {
                     var37 = ((zombie.network.GameServer.DelayedConnection)var36).connection;
                     LoggerManager.getLogger("user").write(var37.idStr + " \"" + var37.username + "\" removed connection index=" + var37.index);
                     udpEngine.connections.remove(var37);
                     disconnect(var37);
                  } else {
                     short var114 = ((ZomboidNetData)var36).type;
                     mainLoopDealWithNetData((ZomboidNetData)var36);
                  }
               }

               synchronized(MainLoopPlayerUpdate) {
                  MainLoopNetData2.clear();
                  MainLoopNetData2.addAll(MainLoopPlayerUpdate.values());
                  MainLoopPlayerUpdate.clear();
               }

               MPStatistic.getInstance().setPacketsLength((long)MainLoopNetData2.size());

               for(var35 = 0; var35 < MainLoopNetData2.size(); ++var35) {
                  var36 = (IZomboidPacket)MainLoopNetData2.get(var35);
                  zombie.network.GameServer.s_performance.mainLoopDealWithNetData.invokeAndMeasure((ZomboidNetData)var36, GameServer::mainLoopDealWithNetData);
               }

               synchronized(MainLoopNetData) {
                  MainLoopNetData2.clear();
                  MainLoopNetData2.addAll(MainLoopNetData);
                  MainLoopNetData.clear();
               }

               MPStatistic.getInstance().setPacketsLength((long)MainLoopNetData2.size());

               for(var35 = 0; var35 < MainLoopNetData2.size(); ++var35) {
                  if (var35 % 10 == 0 && System.currentTimeMillis() - var33 > 70L) {
                     if (droppedPackets == 0) {
                        DebugLog.log("Server is too busy. Server will drop updates of vehicle's physics. Server is closed for new connections.");
                     }

                     droppedPackets += 2;
                     countOfDroppedPackets += MainLoopNetData2.size() - var35;
                     break;
                  }

                  var36 = (IZomboidPacket)MainLoopNetData2.get(var35);
                  zombie.network.GameServer.s_performance.mainLoopDealWithNetData.invokeAndMeasure((ZomboidNetData)var36, GameServer::mainLoopDealWithNetData);
               }

               MainLoopNetData2.clear();
               if (droppedPackets == 1) {
                  DebugLog.log("Server is working normal. Server will not drop updates of vehicle's physics. The server is open for new connections. Server dropped " + countOfDroppedPackets + " packets and " + countOfDroppedConnections + " connections.");
                  countOfDroppedPackets = 0;
                  countOfDroppedConnections = 0;
               }

               droppedPackets = Math.max(0, Math.min(1000, droppedPackets - 1));
               int var113;
               synchronized(consoleCommands) {
                  var113 = 0;

                  while(true) {
                     if (var113 >= consoleCommands.size()) {
                        consoleCommands.clear();
                        break;
                     }

                     String var115 = (String)consoleCommands.get(var113);

                     try {
                        if (CoopSlave.instance == null || !CoopSlave.instance.handleCommand(var115)) {
                           System.out.println(handleServerCommand(var115, (UdpConnection)null));
                        }
                     } catch (Exception var85) {
                        var85.printStackTrace();
                     }

                     ++var113;
                  }
               }

               zombie.network.GameServer.s_performance.RCONServerUpdate.invokeAndMeasure(RCONServer::update);

               try {
                  MapCollisionData.instance.updateGameState();
                  MPStatistic.getInstance().IngameStateUpdate.Start();
                  var99.update();
                  MPStatistic.getInstance().IngameStateUpdate.End();
                  VehicleManager.instance.serverUpdate();
               } catch (Exception var71) {
                  var71.printStackTrace();
               }

               var35 = 0;
               var113 = 0;

               int var116;
               for(var116 = 0; var116 < Players.size(); ++var116) {
                  IsoPlayer var38 = (IsoPlayer)Players.get(var116);
                  if (CheckPlayerStillValid(var38)) {
                     if (!IsoWorld.instance.CurrentCell.getObjectList().contains(var38)) {
                        IsoWorld.instance.CurrentCell.getObjectList().add(var38);
                     }

                     ++var113;
                     if (var38.isAsleep()) {
                        ++var35;
                     }
                  }

                  ServerMap.instance.characterIn(var38);
               }

               setFastForward(ServerOptions.instance.SleepAllowed.getValue() && var113 > 0 && var35 == var113);

               UdpConnection var117;
               for(var116 = 0; var116 < udpEngine.connections.size(); ++var116) {
                  var117 = (UdpConnection)udpEngine.connections.get(var116);

                  for(int var39 = 0; var39 < 4; ++var39) {
                     Vector3 var40 = var117.connectArea[var39];
                     if (var40 != null) {
                        ServerMap.instance.characterIn((int)var40.x, (int)var40.y, (int)var40.z);
                     }

                     ClientServerMap.characterIn(var117, var39);
                  }

                  if (var117.playerDownloadServer != null) {
                     var117.playerDownloadServer.update();
                  }
               }

               for(var116 = 0; var116 < IsoWorld.instance.CurrentCell.getObjectList().size(); ++var116) {
                  IsoMovingObject var118 = (IsoMovingObject)IsoWorld.instance.CurrentCell.getObjectList().get(var116);
                  if (var118 instanceof IsoPlayer && !Players.contains(var118)) {
                     DebugLog.log("Disconnected player in CurrentCell.getObjectList() removed");
                     IsoWorld.instance.CurrentCell.getObjectList().remove(var116--);
                  }
               }

               ++var4;
               if (var4 > 150) {
                  for(var116 = 0; var116 < udpEngine.connections.size(); ++var116) {
                     var117 = (UdpConnection)udpEngine.connections.get(var116);

                     try {
                        if (var117.username == null && !var117.awaitingCoopApprove) {
                           disconnect(var117);
                           udpEngine.forceDisconnect(var117.getConnectedGUID());
                        }
                     } catch (Exception var70) {
                        var70.printStackTrace();
                     }
                  }

                  var4 = 0;
               }

               worldObjectsServerSyncReq.serverSendRequests(udpEngine);
               MPStatistic.getInstance().ServerMapPostupdate.Start();
               ServerMap.instance.postupdate();
               MPStatistic.getInstance().ServerMapPostupdate.End();

               try {
                  ServerGUI.update();
               } catch (Exception var69) {
                  var69.printStackTrace();
               }

               long var14 = System.nanoTime();
               long var16 = var14 - var112;
               long var18 = var25 - var16 - var20;
               MPStatistic.getInstance().Main.StartSleep();
               if (var18 > 0L) {
                  try {
                     Thread.sleep(var18 / 1000000L);
                  } catch (InterruptedException var68) {
                  }

                  var20 = System.nanoTime() - var14 - var18;
               } else {
                  var23 -= var18;
                  var20 = 0L;
                  ++var22;
                  if (var22 >= 5) {
                     Thread.yield();
                     var22 = 0;
                  }
               }

               MPStatistic.getInstance().Main.EndSleep();
               var112 = System.nanoTime();
               long var120 = System.currentTimeMillis();
               long var119 = var120 - var29;
               var108 = 1000.0F / (float)var119;
               if (!Float.isNaN(var108)) {
                  var111 = (float)((double)var111 + Math.min((double)(var108 - var111) * 0.05D, 1.0D));
               }

               GameTime.instance.FPSMultiplier = 60.0F / var111;
               launchCommandHandler();
               MPStatistic.getInstance().process(var119);
               if (!SteamUtils.isSteamModeEnabled()) {
                  PublicServerUtil.update();
                  PublicServerUtil.updatePlayerCountIfChanged();
               }

               for(int var41 = 0; var41 < udpEngine.connections.size(); ++var41) {
                  UdpConnection var42 = (UdpConnection)udpEngine.connections.get(var41);
                  if (var42.accessLevel.equals("admin") && var42.sendPulse && var42.isFullyConnected()) {
                     ByteBufferWriter var43 = var42.startPacket();
                     PacketTypes.doPacket((short)1, var43);
                     var43.putLong(System.currentTimeMillis());
                     var42.endPacket();
                  }

                  if (var42.checksumState == ChecksumState.Different && var42.checksumTime + 8000L < System.currentTimeMillis()) {
                     DebugLog.log("timed out connection because checksum was different");
                     var42.checksumState = ChecksumState.Init;
                     var42.forceDisconnect();
                  } else if (!var42.chunkObjectState.isEmpty()) {
                     for(int var121 = 0; var121 < var42.chunkObjectState.size(); var121 += 2) {
                        short var44 = var42.chunkObjectState.get(var121);
                        short var45 = var42.chunkObjectState.get(var121 + 1);
                        if (!var42.RelevantTo((float)(var44 * 10 + 5), (float)(var45 * 10 + 5), (float)(var42.ChunkGridWidth * 4 * 10))) {
                           var42.chunkObjectState.remove(var121, 2);
                           var121 -= 2;
                        }
                     }
                  }
               }

               if (CoopSlave.instance != null) {
                  CoopSlave.instance.update();
                  if (CoopSlave.instance.masterLost()) {
                     DebugLog.log("Coop master is not responding, terminating");
                     ServerMap.instance.QueueQuit();
                  }
               }

               SteamUtils.runLoop();
               GameWindow.fileSystem.updateAsyncTransactions();
            } finally {
               zombie.network.GameServer.s_performance.frameStep.end();
            }
         }

         CoopSlave.status(Translator.getText("UI_ServerStatus_Terminated"));
         DebugLog.log(DebugType.Network, "Server exited");
         ServerGUI.shutdown();
         ServerPlayerDB.getInstance().close();
         VehiclesDB2.instance.Reset();
         SteamUtils.shutdown();
         System.exit(0);
      }
   }

   private static void launchCommandHandler() {
      if (!launched) {
         launched = true;
         (new Thread(ThreadGroups.Workers, () -> {
            try {
               BufferedReader var0 = new BufferedReader(new InputStreamReader(System.in));

               while(true) {
                  String var1 = var0.readLine();
                  if (var1 == null) {
                     consoleCommands.add("process-status@eof");
                     break;
                  }

                  if (!var1.isEmpty()) {
                     synchronized(consoleCommands) {
                        consoleCommands.add(var1);
                     }
                  }
               }
            } catch (Exception var5) {
               var5.printStackTrace();
            }

         }, "command handler")).start();
      }
   }

   public static String rcon(String var0) {
      try {
         return handleServerCommand(var0, (UdpConnection)null);
      } catch (Throwable var2) {
         var2.printStackTrace();
         return null;
      }
   }

   private static String handleServerCommand(String var0, UdpConnection var1) {
      if (var0 == null) {
         return null;
      } else {
         System.out.println(var0);
         String var2 = "admin";
         String var3 = "admin";
         if (var1 != null) {
            var2 = var1.username;
            var3 = var1.accessLevel;
         }

         if (var1 != null && var1.isCoopHost) {
            var3 = "admin";
         }

         Class var4 = CommandBase.findCommandCls(var0);
         if (var4 != null) {
            Constructor var5 = var4.getConstructors()[0];

            try {
               CommandBase var6 = (CommandBase)var5.newInstance(var2, var3, var0, var1);
               return var6.Execute();
            } catch (InvocationTargetException var7) {
               var7.printStackTrace();
               return "A InvocationTargetException error occured";
            } catch (IllegalAccessException var8) {
               var8.printStackTrace();
               return "A IllegalAccessException error occured";
            } catch (InstantiationException var9) {
               var9.printStackTrace();
               return "A InstantiationException error occured";
            } catch (SQLException var10) {
               var10.printStackTrace();
               return "A SQL error occured";
            }
         } else {
            return "Unknown command " + var0;
         }
      }
   }

   private static void teleport(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);
      float var3 = var0.getFloat();
      float var4 = var0.getFloat();
      float var5 = var0.getFloat();
      IsoPlayer var6 = getPlayerByRealUserName(var2);
      if (var6 != null) {
         UdpConnection var7 = getConnectionFromPlayer(var6);
         if (var7 != null) {
            ByteBufferWriter var8 = var7.startPacket();
            PacketTypes.doPacket((short)108, var8);
            var8.putByte((byte)var6.PlayerIndex);
            var8.putFloat(var3);
            var8.putFloat(var4);
            var8.putFloat(var5);
            var7.endPacketImmediate();
         }
      }
   }

   public static void sendPlayerExtraInfo(IsoPlayer var0, UdpConnection var1) {
      for(int var2 = 0; var2 < udpEngine.connections.size(); ++var2) {
         UdpConnection var3 = (UdpConnection)udpEngine.connections.get(var2);
         ByteBufferWriter var4 = var3.startPacket();
         PacketTypes.doPacket((short)84, var4);
         var4.putShort((short)var0.OnlineID);
         var4.putUTF(var0.accessLevel);
         var4.putByte((byte)(var0.isGodMod() ? 1 : 0));
         var4.putByte((byte)(var0.isGhostMode() ? 1 : 0));
         var4.putByte((byte)(var0.isInvisible() ? 1 : 0));
         var4.putByte((byte)(var0.isNoClip() ? 1 : 0));
         var4.putByte((byte)(var0.isShowAdminTag() ? 1 : 0));
         var3.endPacketImmediate();
      }

   }

   private static void receivePlayerExtraInfo(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      String var3 = GameWindow.ReadString(var0);
      boolean var4 = var0.get() == 1;
      boolean var5 = var0.get() == 1;
      boolean var6 = var0.get() == 1;
      boolean var7 = var0.get() == 1;
      boolean var8 = var0.get() == 1;
      IsoPlayer var9 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var9 != null) {
         var1.accessLevel = var3;
         var9.setGodMod(var4);
         var9.setGhostMode(var5);
         var9.setInvisible(var6);
         var9.setNoClip(var7);
         var9.setShowAdminTag(var8);
         sendPlayerExtraInfo(var9, var1);
      }

   }

   private static void addXpFromPlayerStatsUI(ByteBuffer var0, UdpConnection var1) {
      if (canModifyPlayerStats(var1)) {
         IsoPlayer var2 = (IsoPlayer)IDToPlayerMap.get(var0.getInt());
         int var3 = var0.getInt();
         int var4 = 0;
         int var5 = 0;
         boolean var6 = false;
         if (var2 != null && !var2.isDead() && var3 == 0) {
            var5 = var0.getInt();
            var4 = var0.getInt();
            var6 = var0.get() == 1;
            var2.getXp().AddXP(Perks.fromIndex(var5), (float)var4, false, var6, false, true);
         }

         for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
            if (var8.getConnectedGUID() != var1.getConnectedGUID() && var8.getConnectedGUID() == (Long)PlayerToAddressMap.get(var2)) {
               ByteBufferWriter var9 = var8.startPacket();
               PacketTypes.doPacket((short)124, var9);
               var9.putInt(var2.getOnlineID());
               if (var3 == 0) {
                  var9.putInt(0);
                  var9.putInt(var5);
                  var9.putInt(var4);
                  var9.putByte((byte)(var6 ? 1 : 0));
               }

               var8.endPacketImmediate();
            }
         }

      }
   }

   private static boolean canSeePlayerStats(UdpConnection var0) {
      return !var0.accessLevel.equals("");
   }

   private static boolean canModifyPlayerStats(UdpConnection var0) {
      return var0.accessLevel.equals("admin") || var0.accessLevel.equals("moderator") || var0.accessLevel.equals("overseer");
   }

   private static void syncXp(ByteBuffer var0, UdpConnection var1) {
      if (canModifyPlayerStats(var1)) {
         IsoPlayer var2 = (IsoPlayer)IDToPlayerMap.get(var0.getInt());
         if (var2 != null && !var2.isDead()) {
            try {
               var2.getXp().load(var0, 184);
            } catch (IOException var8) {
               var8.printStackTrace();
            }

            for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
               UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
               if (var4.getConnectedGUID() != var1.getConnectedGUID() && var4.getConnectedGUID() == (Long)PlayerToAddressMap.get(var2)) {
                  ByteBufferWriter var5 = var4.startPacket();
                  PacketTypes.doPacket((short)126, var5);
                  var5.putInt(var2.getOnlineID());

                  try {
                     var2.getXp().save(var5.bb);
                  } catch (IOException var7) {
                     var7.printStackTrace();
                  }

                  var4.endPacketImmediate();
               }
            }
         }

      }
   }

   private static void receivePlayerStatsChanges(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(var2);
      if (var3 != null) {
         String var4 = GameWindow.ReadString(var0);
         var3.setPlayerStats(var0, var4);

         for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
            UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
            if (var6.getConnectedGUID() != var1.getConnectedGUID()) {
               if (var6.getConnectedGUID() == (Long)PlayerToAddressMap.get(var3)) {
                  var6.allChatMuted = var3.isAllChatMuted();
                  var6.accessLevel = var3.accessLevel;
               }

               ByteBufferWriter var7 = var6.startPacket();
               var3.createPlayerStats(var7, var4);
               var6.endPacketImmediate();
            }
         }

      }
   }

   public static void doMinimumInit() throws IOException {
      Rand.init();
      ZomboidFileSystem.instance.init();
      DebugFileWatcher.instance.init();
      ArrayList var0 = new ArrayList(ServerMods);
      ZomboidFileSystem.instance.loadMods(var0);
      LuaManager.init();
      Languages.instance.init();
      Translator.loadFiles();
      PerkFactory.init();
      CustomPerks.instance.init();
      CustomPerks.instance.initLua();
      AssetManagers var1 = GameWindow.assetManagers;
      AiSceneAssetManager.instance.create(AiSceneAsset.ASSET_TYPE, var1);
      AnimationAssetManager.instance.create(AnimationAsset.ASSET_TYPE, var1);
      AnimNodeAssetManager.instance.create(AnimationAsset.ASSET_TYPE, var1);
      ClothingItemAssetManager.instance.create(ClothingItem.ASSET_TYPE, var1);
      MeshAssetManager.instance.create(ModelMesh.ASSET_TYPE, var1);
      ModelAssetManager.instance.create(Model.ASSET_TYPE, var1);
      TextureIDAssetManager.instance.create(TextureID.ASSET_TYPE, var1);
      TextureAssetManager.instance.create(Texture.ASSET_TYPE, var1);
      if (GUICommandline && System.getProperty("softreset") == null) {
         ServerGUI.init();
      }

      CustomSandboxOptions.instance.init();
      CustomSandboxOptions.instance.initInstance(SandboxOptions.instance);
      ScriptManager.instance.Load();
      ClothingDecals.init();
      BeardStyles.init();
      HairStyles.init();
      OutfitManager.init();
      JAssImpImporter.Init();
      ModelManager.NoOpenGL = !ServerGUI.isCreated();
      ModelManager.instance.create();
      System.out.println("LOADING ASSETS: START");

      while(GameWindow.fileSystem.hasWork()) {
         GameWindow.fileSystem.updateAsyncTransactions();
      }

      System.out.println("LOADING ASSETS: FINISH");

      try {
         LuaManager.initChecksum();
         LuaManager.LoadDirBase("shared");
         LuaManager.LoadDirBase("client", true);
         LuaManager.LoadDirBase("server");
         LuaManager.finishChecksum();
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      RecipeManager.LoadedAfterLua();
      String var10002 = ZomboidFileSystem.instance.getCacheDir();
      File var2 = new File(var10002 + File.separator + "Server" + File.separator + ServerName + "_SandboxVars.lua");
      if (var2.exists()) {
         SandboxOptions.instance.loadServerLuaFile(ServerName);
         SandboxOptions.instance.handleOldServerZombiesFile();
         SandboxOptions.instance.toLua();
      } else {
         SandboxOptions.instance.handleOldServerZombiesFile();
         SandboxOptions.instance.saveServerLuaFile(ServerName);
         SandboxOptions.instance.toLua();
      }

      LuaEventManager.triggerEvent("OnGameBoot");
      ZomboidGlobals.Load();
      SpawnPoints.instance.initServer1();
      ServerGUI.init2();
   }

   public static void startServer() throws ConnectException {
      String var0 = ServerOptions.instance.Password.getValue();
      if (CoopSlave.instance != null && SteamUtils.isSteamModeEnabled()) {
         var0 = "";
      }

      udpEngine = new UdpEngine(DEFAULT_PORT, ServerOptions.instance.MaxPlayers.getValue(), var0, true);
      DebugLog.log(DebugType.Network, "*** SERVER STARTED ****");
      DebugLog.log(DebugType.Network, "*** Steam is " + (SteamUtils.isSteamModeEnabled() ? "enabled" : "not enabled"));
      DebugLog.log(DebugType.Network, "server is listening on port " + DEFAULT_PORT);
      ResetID = ServerOptions.instance.ResetID.getValue();
      String var5;
      if (CoopSlave.instance != null) {
         if (SteamUtils.isSteamModeEnabled()) {
            RakNetPeerInterface var1 = udpEngine.getPeer();
            CoopSlave var10000 = CoopSlave.instance;
            String var10003 = var1.GetServerIP();
            var10000.sendMessage("server-address", (String)null, var10003 + ":" + DEFAULT_PORT);
            long var2 = SteamGameServer.GetSteamID();
            CoopSlave.instance.sendMessage("steam-id", (String)null, SteamUtils.convertSteamIDToString(var2));
         } else {
            var5 = "127.0.0.1";
            CoopSlave.instance.sendMessage("server-address", (String)null, var5 + ":" + DEFAULT_PORT);
         }
      }

      LuaEventManager.triggerEvent("OnServerStarted");
      if (SteamUtils.isSteamModeEnabled()) {
         CoopSlave.status("Server Started");
      } else {
         CoopSlave.status("Server Started");
      }

      var5 = ServerOptions.instance.DiscordChannel.getValue();
      String var6 = ServerOptions.instance.DiscordToken.getValue();
      boolean var3 = ServerOptions.instance.DiscordEnable.getValue();
      String var4 = ServerOptions.instance.DiscordChannelID.getValue();
      discordBot.connect(var3, var6, var5, var4);
   }

   private static void mainLoopDealWithNetData(ZomboidNetData var0) {
   }

   private static void invMngRemoveItem(ByteBuffer var0, UdpConnection var1) {
      long var2 = var0.getLong();
      int var4 = var0.getInt();
      IsoPlayer var5 = (IsoPlayer)IDToPlayerMap.get(var4);
      if (var5 != null) {
         for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
            UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
            if (var7.getConnectedGUID() != var1.getConnectedGUID() && var7.getConnectedGUID() == (Long)PlayerToAddressMap.get(var5)) {
               ByteBufferWriter var8 = var7.startPacket();
               PacketTypes.doPacket((short)157, var8);
               var8.putLong(var2);
               var7.endPacketImmediate();
               break;
            }
         }

      }
   }

   private static void invMngGotItem(ByteBuffer var0, UdpConnection var1) throws IOException {
      int var2 = var0.getInt();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(var2);
      if (var3 != null) {
         for(int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
            UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var4);
            if (var5.getConnectedGUID() != var1.getConnectedGUID() && var5.getConnectedGUID() == (Long)PlayerToAddressMap.get(var3)) {
               ByteBufferWriter var6 = var5.startPacket();
               PacketTypes.doPacket((short)156, var6);
               var0.rewind();
               var6.bb.put(var0);
               var5.endPacketImmediate();
               break;
            }
         }

      }
   }

   private static void invMngSendItem(ByteBuffer var0, UdpConnection var1) {
      long var2 = 0L;
      String var4 = null;
      if (var0.get() == 1) {
         var4 = GameWindow.ReadString(var0);
      } else {
         var2 = var0.getLong();
      }

      int var5 = var0.getInt();
      int var6 = var0.getInt();
      IsoPlayer var7 = (IsoPlayer)IDToPlayerMap.get(var6);
      if (var7 != null) {
         for(int var8 = 0; var8 < udpEngine.connections.size(); ++var8) {
            UdpConnection var9 = (UdpConnection)udpEngine.connections.get(var8);
            if (var9.getConnectedGUID() != var1.getConnectedGUID() && var9.getConnectedGUID() == (Long)PlayerToAddressMap.get(var7)) {
               ByteBufferWriter var10 = var9.startPacket();
               PacketTypes.doPacket((short)155, var10);
               if (var4 != null) {
                  var10.putByte((byte)1);
                  var10.putUTF(var4);
               } else {
                  var10.putByte((byte)0);
                  var10.putLong(var2);
               }

               var10.putLong((long)var5);
               var9.endPacketImmediate();
               break;
            }
         }

      }
   }

   private static void sendInventory(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      Long var3 = (Long)IDToAddressMap.get(var2);
      if (var3 != null) {
         for(int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
            UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var4);
            if (var5.getConnectedGUID() == var3) {
               ByteBufferWriter var6 = var5.startPacket();
               PacketTypes.doPacket((short)154, var6);
               var6.bb.put(var0);
               var5.endPacketImmediate();
               break;
            }
         }
      }

   }

   private static void requestInventory(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      Long var4 = (Long)IDToAddressMap.get(var3);
      if (var4 != null) {
         for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
            UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
            if (var6.getConnectedGUID() == var4) {
               ByteBufferWriter var7 = var6.startPacket();
               PacketTypes.doPacket((short)153, var7);
               var7.putInt(var2);
               var6.endPacketImmediate();
               break;
            }
         }
      }

   }

   public static void receiveZombieAttackTarget(ByteBuffer var0) {
      ByteBufferReader var1 = new ByteBufferReader(var0);
      short var2 = var1.getShort();
      short var3 = var1.getShort();
      byte var4 = var1.getByte();
      String var5 = var1.getUTF();

      for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
         UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
         ByteBufferWriter var8 = var7.startPacket();
         PacketTypes.doPacket((short)216, var8);
         var8.putShort(var2);
         var8.putShort(var3);
         var8.putByte(var4);
         var8.putUTF(var5);
         var7.endPacketImmediate();
      }

   }

   public static void receiveEventUpdate(ByteBuffer var0, UdpConnection var1) {
      try {
         EventUpdatePacket var2 = l_receive.eventUpdatePacket;
         var2.parse(var0);
         Iterator var3 = udpEngine.connections.iterator();

         while(var3.hasNext()) {
            UdpConnection var4 = (UdpConnection)var3.next();
            if (var4.getConnectedGUID() != var1.getConnectedGUID() && var4.ReleventTo(var2.event.x, var2.event.y)) {
               ByteBufferWriter var5 = var4.startPacket();
               PacketTypes.doPacket((short)210, var5);
               var2.write(var5);
               var4.endPacketSuperHighUnreliable();
            }
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }

   public static void receiveStatistic(ByteBuffer var0, UdpConnection var1) {
      try {
         var1.statistic.parse(var0);
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public static void receiveStatisticRequest(ByteBuffer var0, UdpConnection var1) {
      if (!var1.accessLevel.equals("admin") && !Core.bDebug) {
         DebugLog.General.error("User " + var1.username + " has no rights to access statistics.");
      } else {
         try {
            var1.statistic.enable = var0.get();
            int var2 = var0.getInt();
            MPStatistic.getInstance().setPeriod(var2);
            sendStatistic(var1);
         } catch (Exception var3) {
            var3.printStackTrace();
         }

      }
   }

   public static void sendShortStatistic() {
      for(int var0 = 0; var0 < udpEngine.connections.size(); ++var0) {
         UdpConnection var1 = (UdpConnection)udpEngine.connections.get(var0);
         if (var1.statistic.enable == 3) {
            sendShortStatistic(var1);
         }
      }

   }

   public static void sendShortStatistic(UdpConnection var0) {
      try {
         ByteBufferWriter var1 = var0.startPacket();
         PacketTypes.doPacket((short)212, var1);
         MPStatistic.getInstance().write(var1);
         var0.endPacketImmediate();
      } catch (Exception var2) {
         var2.printStackTrace();
         var0.cancelPacket();
      }

   }

   public static void sendStatistic() {
      for(int var0 = 0; var0 < udpEngine.connections.size(); ++var0) {
         UdpConnection var1 = (UdpConnection)udpEngine.connections.get(var0);
         if (var1.statistic.enable == 1) {
            sendStatistic(var1);
         }
      }

   }

   public static void sendStatistic(UdpConnection var0) {
      ByteBufferWriter var1 = var0.startPacket();
      PacketTypes.doPacket((short)212, var1);

      try {
         MPStatistic.getInstance().getStatisticTable(var1.bb);
         var0.endPacketImmediate();
      } catch (IOException var3) {
         var3.printStackTrace();
         var0.cancelPacket();
      }

   }

   public static void getStatisticFromClients() {
      try {
         Iterator var0 = udpEngine.connections.iterator();

         while(var0.hasNext()) {
            UdpConnection var1 = (UdpConnection)var0.next();
            ByteBufferWriter var2 = var1.startPacket();
            PacketTypes.doPacket((short)211, var2);
            var2.putLong(System.currentTimeMillis());
            var1.endPacketSuperHighUnreliable();
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public static void receivePlayerUpdate(ByteBuffer var0, UdpConnection var1) {
      if (var1.checksumState != ChecksumState.Done) {
         ByteBufferWriter var8 = var1.startPacket();
         PacketTypes.doPacket((short)83, var8);
         var8.putUTF("You have been kicked from this server.");
         var1.endPacketImmediate();
         var1.forceDisconnect();
      } else {
         PlayerPacket var2 = zombie.network.packets.PlayerPacket.l_receive.playerPacket;
         var2.parse(var0);
         IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2.id));
         if (var3.replay != null) {
            var3.replay.recordPlayerPacket(var2);
            if (var3.replay.isPlay()) {
               return;
            }
         }

         try {
            if (var3 == null) {
               DebugLog.General.error("receivePlayerUpdate: Server received position for unknown player (id:" + var2.id + "). Server will ignore this data.");
            } else {
               var3.networkAI.parse(var2);
               if (var3.networkAI.distance.getLength() > (float)IsoChunkMap.ChunkWidthInTiles) {
                  MPStatistic.getInstance().teleport();
               }

               RakVoice.SetPlayerCoordinate(var1.getConnectedGUID(), var2.realx, var2.realy, (float)var2.realz, var3.isInvisible());
               var1.ReleventPos[var3.PlayerIndex].x = var2.realx;
               var1.ReleventPos[var3.PlayerIndex].y = var2.realy;
               var1.ReleventPos[var3.PlayerIndex].z = (float)var2.realz;
               var2.id = (short)var3.getOnlineID();
            }
         } catch (Exception var7) {
            var7.printStackTrace();
         }

         if (ServerOptions.instance.KickFastPlayers.getValue()) {
            Vector2 var4 = (Vector2)playerToCoordsMap.get(Integer.valueOf(var2.id));
            if (var4 == null) {
               var4 = new Vector2();
               var4.x = var2.x;
               var4.y = var2.y;
               playerToCoordsMap.put(Integer.valueOf(var2.id), var4);
            } else {
               if (!var3.accessLevel.equals("") && !var3.isGhostMode() && (Math.abs(var2.x - var4.x) > 4.0F || Math.abs(var2.y - var4.y) > 4.0F)) {
                  if (playerMovedToFastMap.get(Integer.valueOf(var2.id)) == null) {
                     playerMovedToFastMap.put(Integer.valueOf(var2.id), 1);
                  } else {
                     playerMovedToFastMap.put(Integer.valueOf(var2.id), (Integer)playerMovedToFastMap.get(Integer.valueOf(var2.id)) + 1);
                  }

                  ZLogger var10000 = LoggerManager.getLogger("admin");
                  String var10001 = var3.getDisplayName();
                  var10000.write(var10001 + " go too fast (" + playerMovedToFastMap.get(Integer.valueOf(var2.id)) + " times)");
                  if ((Integer)playerMovedToFastMap.get(Integer.valueOf(var2.id)) == 10) {
                     LoggerManager.getLogger("admin").write(var3.getDisplayName() + " kicked for going too fast");
                     ByteBufferWriter var10 = var1.startPacket();
                     PacketTypes.doPacket((short)83, var10);
                     var10.putUTF("You have been kicked from this server.");
                     var1.endPacketImmediate();
                     var1.forceDisconnect();
                     return;
                  }
               }

               var4.x = var2.x;
               var4.y = var2.y;
            }
         }

         for(int var9 = 0; var9 < udpEngine.connections.size(); ++var9) {
            UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var9);
            if (var1.getConnectedGUID() != var5.getConnectedGUID() && (var5.ReleventTo(var2.x, var2.y) || !ServerOptions.instance.DoLuaChecksum.getValue() && !var5.accessLevel.isEmpty()) && var5.isFullyConnected()) {
               ByteBufferWriter var6 = var5.startPacket();
               PacketTypes.doPacket((short)218, var6);
               var0.position(0);
               var6.bb.put(var0);
               var5.endPacketImmediate();
            }
         }

      }
   }

   private static void receivePacketCounts(ByteBuffer var0, UdpConnection var1) {
      if (!var1.accessLevel.isEmpty()) {
         ByteBufferWriter var2 = var1.startPacket();
         PacketTypes.doPacket((short)47, var2);

         for(int var3 = 0; var3 < 256; ++var3) {
            var2.putLong(packetCounts[var3]);
         }

         var1.endPacket();
      }
   }

   private static void receiveSandboxOptions(ByteBuffer var0) {
      try {
         SandboxOptions.instance.load(var0);
         SandboxOptions.instance.applySettings();
         SandboxOptions.instance.toLua();
         SandboxOptions.instance.saveServerLuaFile(ServerName);

         for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
            UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);
            ByteBufferWriter var3 = var2.startPacket();
            PacketTypes.doPacket((short)31, var3);
            var0.rewind();
            var3.bb.put(var0);
            var2.endPacket();
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   private static void receiveChunkObjectState(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      short var3 = var0.getShort();
      IsoChunk var4 = ServerMap.instance.getChunk(var2, var3);
      if (var4 == null) {
         var1.chunkObjectState.add(var2);
         var1.chunkObjectState.add(var3);
      } else {
         ByteBufferWriter var5 = var1.startPacket();
         PacketTypes.doPacket((short)151, var5);
         var5.putShort(var2);
         var5.putShort(var3);

         try {
            if (var4.saveObjectState(var5.bb)) {
               var1.endPacket();
            } else {
               var1.cancelPacket();
            }
         } catch (Throwable var7) {
            var7.printStackTrace();
            var1.cancelPacket();
            return;
         }
      }

   }

   private static void readAnnotedMap(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);
      StashSystem.prepareBuildingStash(var2);
   }

   private static void tradingUIRemoveItem(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      Long var5 = (Long)IDToAddressMap.get(var3);
      if (var5 != null) {
         for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
            UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
            if (var7.getConnectedGUID() == var5) {
               ByteBufferWriter var8 = var7.startPacket();
               PacketTypes.doPacket((short)148, var8);
               var8.putInt(var2);
               var8.putInt(var4);
               var7.endPacketImmediate();
               break;
            }
         }
      }

   }

   private static void tradingUIUpdateState(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      Long var5 = (Long)IDToAddressMap.get(var3);
      if (var5 != null) {
         for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
            UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
            if (var7.getConnectedGUID() == var5) {
               ByteBufferWriter var8 = var7.startPacket();
               PacketTypes.doPacket((short)149, var8);
               var8.putInt(var2);
               var8.putInt(var4);
               var7.endPacketImmediate();
               break;
            }
         }
      }

   }

   private static void tradingUIAddItem(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      InventoryItem var4 = null;

      try {
         var4 = InventoryItem.loadItem(var0, 184);
      } catch (Exception var11) {
         var11.printStackTrace();
      }

      if (var4 != null) {
         Long var5 = (Long)IDToAddressMap.get(var3);
         if (var5 != null) {
            for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
               UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
               if (var7.getConnectedGUID() == var5) {
                  ByteBufferWriter var8 = var7.startPacket();
                  PacketTypes.doPacket((short)147, var8);
                  var8.putInt(var2);

                  try {
                     var4.saveWithSize(var8.bb, false);
                  } catch (IOException var10) {
                     var10.printStackTrace();
                  }

                  var7.endPacketImmediate();
                  break;
               }
            }
         }

      }
   }

   private static void requestTrading(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      byte var4 = var0.get();
      Long var5 = (Long)IDToAddressMap.get(var2);
      if (var4 == 0) {
         var5 = (Long)IDToAddressMap.get(var3);
      }

      if (var5 != null) {
         for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
            UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
            if (var7.getConnectedGUID() == var5) {
               ByteBufferWriter var8 = var7.startPacket();
               PacketTypes.doPacket((short)146, var8);
               if (var4 == 0) {
                  var8.putInt(var2);
               } else {
                  var8.putInt(var3);
               }

               var8.putByte(var4);
               var7.endPacketImmediate();
               break;
            }
         }
      }

   }

   private static void syncFaction(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);
      String var3 = GameWindow.ReadString(var0);
      int var4 = var0.getInt();
      Faction var5 = Faction.getFaction(var2);
      boolean var6 = false;
      if (var5 == null) {
         var5 = new Faction(var2, var3);
         var6 = true;
         Faction.getFactions().add(var5);
      }

      var5.getPlayers().clear();
      if (var0.get() == 1) {
         var5.setTag(GameWindow.ReadString(var0));
         var5.setTagColor(new ColorInfo(var0.getFloat(), var0.getFloat(), var0.getFloat(), 1.0F));
      }

      for(int var7 = 0; var7 < var4; ++var7) {
         String var8 = GameWindow.ReadString(var0);
         var5.getPlayers().add(var8);
      }

      if (!var5.getOwner().equals(var3)) {
         var5.setOwner(var3);
      }

      boolean var11 = var0.get() == 1;
      if (ChatServer.isInited()) {
         if (var6) {
            ChatServer.getInstance().createFactionChat(var2);
         }

         if (var11) {
            ChatServer.getInstance().removeFactionChat(var2);
         } else {
            ChatServer.getInstance().syncFactionChatMembers(var2, var3, var5.getPlayers());
         }
      }

      if (var11) {
         Faction.getFactions().remove(var5);
         DebugLog.log("faction: removed " + var2 + " owner=" + var5.getOwner());
      }

      for(int var12 = 0; var12 < udpEngine.connections.size(); ++var12) {
         UdpConnection var9 = (UdpConnection)udpEngine.connections.get(var12);
         if (var1 == null || var9.getConnectedGUID() != var1.getConnectedGUID()) {
            ByteBufferWriter var10 = var9.startPacket();
            PacketTypes.doPacket((short)140, var10);
            var5.writeToBuffer(var10, var11);
            var9.endPacketImmediate();
         }
      }

   }

   private static void syncNonPvpZone(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      String var6 = GameWindow.ReadString(var0);
      NonPvpZone var7 = NonPvpZone.getZoneByTitle(var6);
      if (var7 == null) {
         var7 = NonPvpZone.addNonPvpZone(var6, var2, var3, var4, var5);
      }

      if (var7 != null) {
         boolean var8 = var0.get() == 1;
         sendNonPvpZone(var7, var8, var1);
         if (var8) {
            NonPvpZone.removeNonPvpZone(var6, true);
            DebugLog.log("non pvp zone: removed " + var2 + "," + var3 + ", ttle=" + var7.getTitle());
         }

      }
   }

   public static void sendNonPvpZone(NonPvpZone var0, boolean var1, UdpConnection var2) {
      for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
         if (var2 == null || var4.getConnectedGUID() != var2.getConnectedGUID()) {
            ByteBufferWriter var5 = var4.startPacket();
            PacketTypes.doPacket((short)139, var5);
            var5.putInt(var0.getX());
            var5.putInt(var0.getY());
            var5.putInt(var0.getX2());
            var5.putInt(var0.getY2());
            var5.putUTF(var0.getTitle());
            var5.putBoolean(var1);
            var4.endPacketImmediate();
         }
      }

   }

   private static void receiveTextColor(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(var2);
      if (var3 != null) {
         float var4 = var0.getFloat();
         float var5 = var0.getFloat();
         float var6 = var0.getFloat();
         var3.setSpeakColourInfo(new ColorInfo(var4, var5, var6, 1.0F));

         for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
            if (var8.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var9 = var8.startPacket();
               PacketTypes.doPacket((short)138, var9);
               var9.putInt(var2);
               var9.putFloat(var4);
               var9.putFloat(var5);
               var9.putFloat(var6);
               var8.endPacketImmediate();
            }
         }

      }
   }

   private static void receiveTransactionID(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      IsoPlayer var4 = (IsoPlayer)IDToPlayerMap.get(var2);
      if (var4 != null) {
         transactionIDMap.put(var4.username, var3);
         var4.setTransactionID(var3);
         ServerWorldDatabase.instance.saveTransactionID(var4.username, var3);
      }

   }

   private static void syncCompost(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var5 != null) {
         IsoCompost var6 = var5.getCompost();
         if (var6 == null) {
            var6 = new IsoCompost(var5.getCell(), var5);
            var5.AddSpecialObject(var6);
         }

         float var7 = var0.getFloat();
         var6.setCompost(var7);
         sendCompost(var6, var1);
      }

   }

   public static void sendCompost(IsoCompost var0, UdpConnection var1) {
      for(int var2 = 0; var2 < udpEngine.connections.size(); ++var2) {
         UdpConnection var3 = (UdpConnection)udpEngine.connections.get(var2);
         if (var3.ReleventTo((float)var0.square.x, (float)var0.square.y) && (var1 != null && var3.getConnectedGUID() != var1.getConnectedGUID() || var1 == null)) {
            ByteBufferWriter var4 = var3.startPacket();
            PacketTypes.doPacket((short)122, var4);
            var4.putInt(var0.square.x);
            var4.putInt(var0.square.y);
            var4.putInt(var0.square.z);
            var4.putFloat(var0.getCompost());
            var3.endPacketImmediate();
         }
      }

   }

   private static void doCataplasm(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var0.getInt();
         float var5 = var0.getFloat();
         float var6 = var0.getFloat();
         float var7 = var0.getFloat();
         if (var5 > 0.0F) {
            var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setPlantainFactor(var5);
         }

         if (var6 > 0.0F) {
            var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setComfreyFactor(var6);
         }

         if (var7 > 0.0F) {
            var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setGarlicFactor(var7);
         }

         for(int var8 = 0; var8 < udpEngine.connections.size(); ++var8) {
            UdpConnection var9 = (UdpConnection)udpEngine.connections.get(var8);
            if (var9.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var10 = var9.startPacket();
               PacketTypes.doPacket((short)117, var10);
               var10.putShort(var2);
               var10.putInt(var4);
               var10.putFloat(var5);
               var10.putFloat(var6);
               var10.putFloat(var7);
               var9.endPacketImmediate();
            }
         }
      }

   }

   private static void destroy(ByteBuffer var0, UdpConnection var1) {
      if (ServerOptions.instance.AllowDestructionBySledgehammer.getValue()) {
         RemoveItemFromMap(var0, var1);
      }

   }

   public static void AddExplosiveTrap(HandWeapon var0, IsoGridSquare var1, boolean var2) {
      IsoTrap var3 = new IsoTrap(var0, var1.getCell(), var1);
      int var4 = 0;
      if (var0.getExplosionRange() > 0) {
         var4 = var0.getExplosionRange();
      }

      if (var0.getFireRange() > 0) {
         var4 = var0.getFireRange();
      }

      if (var0.getSmokeRange() > 0) {
         var4 = var0.getSmokeRange();
      }

      var1.AddTileObject(var3);

      for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
         UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
         ByteBufferWriter var7 = var6.startPacket();
         PacketTypes.doPacket((short)110, var7);
         var7.putInt(var1.x);
         var7.putInt(var1.y);
         var7.putInt(var1.z);

         try {
            var0.saveWithSize(var7.bb, false);
         } catch (IOException var9) {
            var9.printStackTrace();
         }

         var7.putInt(var4);
         var7.putBoolean(var2);
         var7.putBoolean(false);
         var6.endPacketImmediate();
      }

   }

   private static void AddExplosiveTrap(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var5 != null) {
         InventoryItem var6 = null;

         try {
            var6 = InventoryItem.loadItem(var0, 184);
         } catch (Exception var14) {
            var14.printStackTrace();
         }

         if (var6 == null) {
            return;
         }

         HandWeapon var7 = (HandWeapon)var6;
         String var10000 = var1.username;
         DebugLog.log("trap: user \"" + var10000 + "\" added " + var6.getFullType() + " at " + var2 + "," + var3 + "," + var4);
         ZLogger var15 = LoggerManager.getLogger("map");
         String var10001 = var1.idStr;
         var15.write(var10001 + " \"" + var1.username + "\" added " + var6.getFullType() + " at " + var2 + "," + var3 + "," + var4);
         IsoTrap var8 = new IsoTrap(var7, var5.getCell(), var5);
         if (var7.getExplosionTimer() <= 0 && var7.getSensorRange() <= 0 && var7.getRemoteControlID() == -1) {
            var8.triggerExplosion(false);
            var8.removeFromWorld();
         } else {
            var5.AddTileObject(var8);

            for(int var9 = 0; var9 < udpEngine.connections.size(); ++var9) {
               UdpConnection var10 = (UdpConnection)udpEngine.connections.get(var9);
               if (var10.getConnectedGUID() != var1.getConnectedGUID()) {
                  ByteBufferWriter var11 = var10.startPacket();
                  PacketTypes.doPacket((short)110, var11);
                  var11.putInt(var2);
                  var11.putInt(var3);
                  var11.putInt(var4);

                  try {
                     var7.saveWithSize(var11.bb, false);
                  } catch (IOException var13) {
                     var13.printStackTrace();
                  }

                  var10.endPacketImmediate();
               }
            }
         }
      }

   }

   public static void sendHelicopter(float var0, float var1, boolean var2) {
      for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
         ByteBufferWriter var5 = var4.startPacket();
         PacketTypes.doPacket((short)11, var5);
         var5.putFloat(var0);
         var5.putFloat(var1);
         var5.putBoolean(var2);
         var4.endPacketImmediate();
      }

   }

   private static void registerZone(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);
      String var3 = GameWindow.ReadString(var0);
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      int var6 = var0.getInt();
      int var7 = var0.getInt();
      int var8 = var0.getInt();
      int var9 = var0.getInt();
      boolean var10 = var0.get() == 1;
      ArrayList var11 = IsoWorld.instance.getMetaGrid().getZonesAt(var4, var5, var6);
      boolean var12 = false;
      Iterator var13 = var11.iterator();

      while(var13.hasNext()) {
         Zone var14 = (Zone)var13.next();
         if (var3.equals(var14.getType())) {
            var12 = true;
            var14.setName(var2);
            var14.setLastActionTimestamp(var9);
         }
      }

      if (!var12) {
         IsoWorld.instance.getMetaGrid().registerZone(var2, var3, var4, var5, var6, var7, var8);
      }

      if (var10) {
         for(int var16 = 0; var16 < udpEngine.connections.size(); ++var16) {
            UdpConnection var17 = (UdpConnection)udpEngine.connections.get(var16);
            if (var17.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var15 = var17.startPacket();
               PacketTypes.doPacket((short)94, var15);
               var15.putUTF(var2);
               var15.putUTF(var3);
               var15.putInt(var4);
               var15.putInt(var5);
               var15.putInt(var6);
               var15.putInt(var7);
               var15.putInt(var8);
               var15.putInt(var9);
               var17.endPacketImmediate();
            }
         }
      }

   }

   public static void sendZone(Zone var0, UdpConnection var1) {
      for(int var2 = 0; var2 < udpEngine.connections.size(); ++var2) {
         UdpConnection var3 = (UdpConnection)udpEngine.connections.get(var2);
         if (var1 == null || var3.getConnectedGUID() != var1.getConnectedGUID()) {
            ByteBufferWriter var4 = var3.startPacket();
            PacketTypes.doPacket((short)94, var4);
            var4.putUTF(var0.name);
            var4.putUTF(var0.type);
            var4.putInt(var0.x);
            var4.putInt(var0.y);
            var4.putInt(var0.z);
            var4.putInt(var0.w);
            var4.putInt(var0.h);
            var4.putInt(var0.lastActionTimestamp);
            var3.endPacketImmediate();
         }
      }

   }

   private static void constructedZone(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      Zone var5 = IsoWorld.instance.MetaGrid.getZoneAt(var2, var3, var4);
      if (var5 != null) {
         var5.setHaveConstruction(true);
      }

   }

   public static void addXp(IsoPlayer var0, Perk var1, int var2) {
      if (PlayerToAddressMap.containsKey(var0)) {
         long var3 = (Long)PlayerToAddressMap.get(var0);
         UdpConnection var5 = udpEngine.getActiveConnection(var3);
         if (var5 == null) {
            return;
         }

         ByteBufferWriter var6 = var5.startPacket();
         PacketTypes.doPacket((short)89, var6);
         var6.putByte((byte)var0.PlayerIndex);
         var6.putInt(var1.index());
         var6.putInt(var2);
         var5.endPacketImmediate();
      }

   }

   private static void log(ByteBuffer var0, UdpConnection var1) {
      LoggerManager.getLogger(GameWindow.ReadString(var0)).write(GameWindow.ReadString(var0));
   }

   private static void answerPing(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);

      for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
         if (var4.getConnectedGUID() == var1.getConnectedGUID()) {
            ByteBufferWriter var5 = var4.startPacket();
            PacketTypes.doPacket((short)87, var5);
            var5.putUTF(var2);
            var5.putInt(udpEngine.connections.size());
            var5.putInt(512);
            var4.endPacketImmediate();
         }
      }

   }

   private static void updateItemSprite(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      String var3 = GameWindow.ReadStringUTF(var0);
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      int var6 = var0.getInt();
      int var7 = var0.getInt();
      IsoGridSquare var8 = IsoWorld.instance.CurrentCell.getGridSquare(var4, var5, var6);
      if (var8 != null && var7 < var8.getObjects().size()) {
         try {
            IsoObject var9 = (IsoObject)var8.getObjects().get(var7);
            if (var9 != null) {
               var9.sprite = IsoSpriteManager.instance.getSprite(var2);
               if (var9.sprite == null && !var3.isEmpty()) {
                  var9.setSprite(var3);
               }

               var9.RemoveAttachedAnims();
               int var10 = var0.get() & 255;

               for(int var11 = 0; var11 < var10; ++var11) {
                  int var12 = var0.getInt();
                  IsoSprite var13 = IsoSpriteManager.instance.getSprite(var12);
                  if (var13 != null) {
                     var9.AttachExistingAnim(var13, 0, 0, false, 0, false, 0.0F);
                  }
               }

               var9.transmitUpdatedSpriteToClients(var1);
            }
         } catch (Exception var14) {
         }
      }

   }

   private static void sendWorldMessage(ByteBuffer var0, UdpConnection var1) {
      if (!var1.allChatMuted) {
         String var2 = GameWindow.ReadString(var0);
         String var3 = GameWindow.ReadString(var0);
         if (var3.length() > 256) {
            var3 = var3.substring(0, 256);
         }

         for(int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
            UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var4);
            ByteBufferWriter var6 = var5.startPacket();
            PacketTypes.doPacket((short)79, var6);
            var6.putUTF(var2);
            var6.putUTF(var3);
            var5.endPacketImmediate();
         }

         discordBot.sendMessage(var2, var3);
         LoggerManager.getLogger("chat").write(var1.index + " \"" + var1.username + "\" A \"" + var3 + "\"");
      }
   }

   private static void sendCustomModDataToClient(UdpConnection var0) {
      LuaEventManager.triggerEvent("SendCustomModData");
   }

   public static void stopFire(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      short var10;
      if (var2 == 1) {
         var10 = var0.getShort();
         IsoPlayer var12 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var10));
         if (var12 != null) {
            var12.sendObjectChange("StopBurning");
         }

      } else if (var2 == 2) {
         var10 = var0.getShort();
         IsoZombie var11 = ServerMap.instance.ZombieMap.get(var10);
         if (var11 != null) {
            var11.StopBurning();
         }

      } else {
         int var3 = var0.getInt();
         int var4 = var0.getInt();
         int var5 = var0.getInt();
         IsoGridSquare var6 = ServerMap.instance.getGridSquare(var3, var4, var5);
         if (var6 != null) {
            var6.stopFire();

            for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
               UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
               if (var8.ReleventTo((float)var3, (float)var4) && var8.getConnectedGUID() != var1.getConnectedGUID()) {
                  ByteBufferWriter var9 = var8.startPacket();
                  PacketTypes.doPacket((short)116, var9);
                  var9.putInt(var3);
                  var9.putInt(var4);
                  var9.putInt(var5);
                  var8.endPacketImmediate();
               }
            }

         }
      }
   }

   public static void startFireOnClient(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      boolean var6 = var0.get() == 1;
      int var7 = var0.getInt();
      boolean var8 = var0.get() == 1;
      if (!var8 && ServerOptions.instance.NoFire.getValue()) {
         DebugLog.log("user \"" + var1.username + "\" tried to start a fire");
      } else {
         IsoGridSquare var9 = ServerMap.instance.getGridSquare(var2, var3, var4);
         if (var9 != null) {
            IsoFire var10 = var8 ? new IsoFire(var9.getCell(), var9, var6, var5, var7, true) : new IsoFire(var9.getCell(), var9, var6, var5, var7);
            IsoFireManager.Add(var10);
            var9.getObjects().add(var10);

            for(int var11 = 0; var11 < udpEngine.connections.size(); ++var11) {
               UdpConnection var12 = (UdpConnection)udpEngine.connections.get(var11);
               if (var12.ReleventTo((float)var2, (float)var3)) {
                  ByteBufferWriter var13 = var12.startPacket();
                  PacketTypes.doPacket((short)75, var13);
                  var13.putInt(var2);
                  var13.putInt(var3);
                  var13.putInt(var4);
                  var13.putInt(var5);
                  var13.putBoolean(var6);
                  var13.putInt(var10.SpreadDelay);
                  var13.putInt(var10.Life);
                  var13.putInt(var10.numFlameParticles);
                  var13.putBoolean(var8);
                  var12.endPacketImmediate();
               }
            }

         }
      }
   }

   public static void startFireOnClient(IsoGridSquare var0, int var1, boolean var2, int var3, boolean var4) {
      IsoFire var5 = var4 ? new IsoFire(var0.getCell(), var0, var2, var1, var3, true) : new IsoFire(var0.getCell(), var0, var2, var1, var3);
      IsoFireManager.Add(var5);
      var0.getObjects().add(var5);

      for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
         UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
         if (var7.ReleventTo((float)var0.getX(), (float)var0.getY())) {
            ByteBufferWriter var8 = var7.startPacket();
            PacketTypes.doPacket((short)75, var8);
            var8.putInt(var0.getX());
            var8.putInt(var0.getY());
            var8.putInt(var0.getZ());
            var8.putInt(var1);
            var8.putBoolean(var2);
            var8.putInt(var5.SpreadDelay);
            var8.putInt(var5.Life);
            var8.putInt(var5.numFlameParticles);
            var8.putBoolean(var4);
            var7.endPacketImmediate();
         }
      }

   }

   public static void sendOptionsToClients() {
      for(int var0 = 0; var0 < udpEngine.connections.size(); ++var0) {
         UdpConnection var1 = (UdpConnection)udpEngine.connections.get(var0);
         ByteBufferWriter var2 = var1.startPacket();
         PacketTypes.doPacket((short)82, var2);
         var2.putInt(ServerOptions.instance.getPublicOptions().size());
         String var3 = null;
         Iterator var4 = ServerOptions.instance.getPublicOptions().iterator();

         while(var4.hasNext()) {
            var3 = (String)var4.next();
            var2.putUTF(var3);
            var2.putUTF(ServerOptions.instance.getOption(var3));
         }

         var1.endPacketImmediate();
      }

   }

   public static void sendCorpse(IsoDeadBody var0) {
      IsoGridSquare var1 = var0.getSquare();
      if (var1 != null) {
         for(int var2 = 0; var2 < udpEngine.connections.size(); ++var2) {
            UdpConnection var3 = (UdpConnection)udpEngine.connections.get(var2);
            if (var3.ReleventTo((float)var1.x, (float)var1.y)) {
               ByteBufferWriter var4 = var3.startPacket();
               PacketTypes.doPacket((short)69, var4);
               var4.putInt(var1.x);
               var4.putInt(var1.y);
               var4.putInt(var1.z);
               var0.writeToRemoteBuffer(var4);
               var3.endPacketImmediate();
            }
         }

      }
   }

   private static void addCorpseToMap(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      IsoObject var5 = WorldItemTypes.createFromBuffer(var0);
      if (var5 != null && var5 instanceof IsoDeadBody) {
         var5.loadFromRemoteBuffer(var0, false);
         IsoGridSquare var6 = ServerMap.instance.getGridSquare(var2, var3, var4);
         if (var6 != null) {
            var6.addCorpse((IsoDeadBody)var5, true);

            for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
               UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
               if (var8.getConnectedGUID() != var1.getConnectedGUID() && var8.ReleventTo((float)var2, (float)var3)) {
                  ByteBufferWriter var9 = var8.startPacket();
                  PacketTypes.doPacket((short)69, var9);
                  var0.rewind();
                  var9.bb.put(var0);
                  var8.endPacketImmediate();
               }
            }
         }

         LoggerManager.getLogger("item").write(var1.idStr + " \"" + var1.username + "\" corpse +1 " + var2 + "," + var3 + "," + var4);
      }
   }

   public static void removeCorpseFromMap(IsoDeadBody var0) {
      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);
         if (var0.getSquare() != null && var2.ReleventTo(var0.getX(), var0.getY())) {
            ByteBufferWriter var3 = var2.startPacket();
            PacketTypes.doPacket((short)68, var3);
            var3.putInt(var0.getSquare().getX());
            var3.putInt(var0.getSquare().getY());
            var3.putInt(var0.getSquare().getZ());
            var3.putInt(var0.getSquare().getStaticMovingObjects().indexOf(var0));
            var3.putInt(var0.getOnlineId());
            var2.endPacketImmediate();
         }
      }

      IsoPlayer var4 = (IsoPlayer)IDToPlayerMap.get(var0.getOnlineId());
      if (var4 != null) {
         PlayerToBody.remove(var4);
      }

   }

   private static void removeCorpseFromMap(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      int var6 = var0.getInt();
      IsoGridSquare var7 = ServerMap.instance.getGridSquare(var2, var3, var4);
      if (var7 != null && var5 >= 0 && var5 < var7.getStaticMovingObjects().size()) {
         IsoObject var8 = (IsoObject)var7.getStaticMovingObjects().get(var5);
         var7.removeCorpse((IsoDeadBody)var8, true);

         for(int var9 = 0; var9 < udpEngine.connections.size(); ++var9) {
            UdpConnection var10 = (UdpConnection)udpEngine.connections.get(var9);
            if (var10.getConnectedGUID() != var1.getConnectedGUID() && var10.ReleventTo((float)var2, (float)var3)) {
               ByteBufferWriter var11 = var10.startPacket();
               PacketTypes.doPacket((short)68, var11);
               var0.rewind();
               var11.bb.put(var0);
               var10.endPacketImmediate();
            }
         }
      }

      IsoPlayer var12 = (IsoPlayer)IDToPlayerMap.get(var6);
      if (var12 != null) {
         PlayerToBody.remove(var12);
      }

      LoggerManager.getLogger("item").write(var1.idStr + " \"" + var1.username + "\" corpse -1 " + var2 + "," + var3 + "," + var4);
   }

   private static void sendPlayerConnect(IsoPlayer var0, UdpConnection var1) {
      ByteBufferWriter var2 = var1.startPacket();
      PacketTypes.doPacket((short)6, var2);
      if (var1.getConnectedGUID() != (Long)PlayerToAddressMap.get(var0)) {
         var2.putShort((short)var0.OnlineID);
      } else {
         var2.putShort((short)-1);
         var2.putByte((byte)var0.PlayerIndex);
         var2.putShort((short)var0.OnlineID);

         try {
            GameTime.getInstance().saveToPacket(var2.bb);
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      }

      var2.putFloat(var0.x);
      var2.putFloat(var0.y);
      var2.putFloat(var0.z);
      var2.putUTF(var0.username);
      if (var1.getConnectedGUID() != (Long)PlayerToAddressMap.get(var0)) {
         try {
            var0.getDescriptor().save(var2.bb);
            var0.getHumanVisual().save(var2.bb);
            ItemVisuals var3 = new ItemVisuals();
            var0.getItemVisuals(var3);
            var3.save(var2.bb);
         } catch (IOException var5) {
            var5.printStackTrace();
         }
      }

      if (SteamUtils.isSteamModeEnabled()) {
         var2.putLong(var0.getSteamID());
      }

      var2.putByte((byte)(var0.isGodMod() ? 1 : 0));
      var2.putByte((byte)(var0.isGhostMode() ? 1 : 0));
      var2.putByte((byte)(var0.isSafety() ? 1 : 0));
      var2.putUTF(var0.accessLevel);
      var2.putByte((byte)(var0.isInvisible() ? 1 : 0));
      if (var1.getConnectedGUID() != (Long)PlayerToAddressMap.get(var0) && canSeePlayerStats(var1)) {
         try {
            var0.getXp().save(var2.bb);
         } catch (IOException var4) {
            var4.printStackTrace();
         }
      }

      var2.putUTF(var0.getTagPrefix());
      var2.putFloat(var0.getTagColor().r);
      var2.putFloat(var0.getTagColor().g);
      var2.putFloat(var0.getTagColor().b);
      var2.putDouble(var0.getHoursSurvived());
      var2.putInt(var0.getZombieKills());
      var2.putUTF(var0.getDisplayName());
      var2.putFloat(var0.getSpeakColour().r);
      var2.putFloat(var0.getSpeakColour().g);
      var2.putFloat(var0.getSpeakColour().b);
      var2.putBoolean(var0.showTag);
      var2.putBoolean(var0.factionPvp);
      var2.putInt(var0.getAttachedItems().size());

      for(int var7 = 0; var7 < var0.getAttachedItems().size(); ++var7) {
         var2.putUTF(var0.getAttachedItems().get(var7).getLocation());
         var2.putUTF(var0.getAttachedItems().get(var7).getItem().getFullType());
      }

      var2.putInt(var0.remoteSneakLvl);
      var2.putInt(var0.remoteStrLvl);
      var2.putInt(var0.remoteFitLvl);
      var1.endPacketImmediate();
      if (var1.getConnectedGUID() != (Long)PlayerToAddressMap.get(var0)) {
         updateHandEquips(var1, var0);
      }

   }

   private static void RequestPlayerData(ByteBuffer var0, UdpConnection var1) {
      IsoPlayer var2 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var0.getShort()));
      if (var2 != null) {
         sendPlayerConnect(var2, var1);
      }

   }

   private static void SyncPlayerInventory(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      IsoPlayer var3 = getPlayerFromConnection(var1, var2);
      if (var3 != null) {
         DebugLog.log("SyncPlayerInventory " + var3.username);
         var3.setInventory(new ItemContainer());
         var3.clearWornItems();

         int var6;
         try {
            ArrayList var4 = var3.getInventory().load(var0, 184);
            byte var5 = var0.get();

            for(var6 = 0; var6 < var5; ++var6) {
               String var7 = GameWindow.ReadString(var0);
               short var8 = var0.getShort();
               if (var8 >= 0 && var8 < var4.size() && var3.getBodyLocationGroup().getLocation(var7) != null) {
                  var3.setWornItem(var7, (InventoryItem)var4.get(var8));
               }
            }
         } catch (IOException var10) {
            var10.printStackTrace();
         }

         IsoDeadBody var11 = (IsoDeadBody)PlayerToBody.get(var3);
         if (var11 != null) {
            var11.setContainer(var3.getInventory());
            var11.setWornItems(var3.getWornItems());
            var3.setInventory(new ItemContainer());
            var3.clearWornItems();
            int var12 = (int)var3.x;
            var6 = (int)var3.y;

            for(int var13 = 0; var13 < udpEngine.connections.size(); ++var13) {
               UdpConnection var14 = (UdpConnection)udpEngine.connections.get(var13);
               if (var14.getConnectedGUID() != var1.getConnectedGUID() && var14.ReleventTo((float)var12, (float)var6)) {
                  ByteBufferWriter var9 = var14.startPacket();
                  PacketTypes.doPacket((short)65, var9);
                  var0.rewind();
                  var9.putShort((short)var3.OnlineID);
                  var9.bb.put(var0);
                  var14.endPacketImmediate();
               }
            }

            PlayerToBody.remove(var3);
         }
      }

   }

   public static void loadModData(IsoGridSquare var0) {
      if (var0.getModData().rawget("id") != null && var0.getModData().rawget("id") != null && (var0.getModData().rawget("remove") == null || ((String)var0.getModData().rawget("remove")).equals("false"))) {
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":x", new Double((double)var0.getX()));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":y", new Double((double)var0.getY()));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":z", new Double((double)var0.getZ()));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":typeOfSeed", var0.getModData().rawget("typeOfSeed"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":nbOfGrow", (Double)var0.getModData().rawget("nbOfGrow"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":id", var0.getModData().rawget("id"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":waterLvl", var0.getModData().rawget("waterLvl"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":lastWaterHour", var0.getModData().rawget("lastWaterHour"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":waterNeeded", var0.getModData().rawget("waterNeeded"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":waterNeededMax", var0.getModData().rawget("waterNeededMax"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":mildewLvl", var0.getModData().rawget("mildewLvl"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":aphidLvl", var0.getModData().rawget("aphidLvl"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":fliesLvl", var0.getModData().rawget("fliesLvl"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":fertilizer", var0.getModData().rawget("fertilizer"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":nextGrowing", var0.getModData().rawget("nextGrowing"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":hasVegetable", var0.getModData().rawget("hasVegetable"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":hasSeed", var0.getModData().rawget("hasSeed"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":health", var0.getModData().rawget("health"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":badCare", var0.getModData().rawget("badCare"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":state", var0.getModData().rawget("state"));
         if (var0.getModData().rawget("hoursElapsed") != null) {
            GameTime.getInstance().getModData().rawset("hoursElapsed", var0.getModData().rawget("hoursElapsed"));
         }
      }

      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);
         if (var2.ReleventTo((float)var0.getX(), (float)var0.getY())) {
            ByteBufferWriter var3 = var2.startPacket();
            PacketTypes.doPacket((short)51, var3);
            var3.putInt(var0.getX());
            var3.putInt(var0.getY());
            var3.putInt(var0.getZ());

            try {
               var0.getModData().save(var3.bb);
            } catch (IOException var5) {
               var5.printStackTrace();
            }

            var2.endPacketImmediate();
         }
      }

   }

   private static void loadModData(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      IsoGridSquare var5 = ServerMap.instance.getGridSquare(var2, var3, var4);
      if (var5 != null) {
         try {
            var5.getModData().load(var0, 184);
            if (var5.getModData().rawget("id") != null && (var5.getModData().rawget("remove") == null || ((String)var5.getModData().rawget("remove")).equals("false"))) {
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":x", new Double((double)var5.getX()));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":y", new Double((double)var5.getY()));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":z", new Double((double)var5.getZ()));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":typeOfSeed", var5.getModData().rawget("typeOfSeed"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":nbOfGrow", (Double)var5.getModData().rawget("nbOfGrow"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":id", var5.getModData().rawget("id"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":waterLvl", var5.getModData().rawget("waterLvl"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":lastWaterHour", var5.getModData().rawget("lastWaterHour"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":waterNeeded", var5.getModData().rawget("waterNeeded"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":waterNeededMax", var5.getModData().rawget("waterNeededMax"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":mildewLvl", var5.getModData().rawget("mildewLvl"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":aphidLvl", var5.getModData().rawget("aphidLvl"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":fliesLvl", var5.getModData().rawget("fliesLvl"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":fertilizer", var5.getModData().rawget("fertilizer"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":nextGrowing", var5.getModData().rawget("nextGrowing"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":hasVegetable", var5.getModData().rawget("hasVegetable"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":hasSeed", var5.getModData().rawget("hasSeed"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":health", var5.getModData().rawget("health"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":badCare", var5.getModData().rawget("badCare"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":state", var5.getModData().rawget("state"));
               if (var5.getModData().rawget("hoursElapsed") != null) {
                  GameTime.getInstance().getModData().rawset("hoursElapsed", var5.getModData().rawget("hoursElapsed"));
               }
            }

            LuaEventManager.triggerEvent("onLoadModDataFromServer", var5);

            for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
               UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
               if (var7.ReleventTo((float)var5.getX(), (float)var5.getY()) && (var1 == null || var7.getConnectedGUID() != var1.getConnectedGUID())) {
                  ByteBufferWriter var8 = var7.startPacket();
                  PacketTypes.doPacket((short)51, var8);
                  var8.putInt(var2);
                  var8.putInt(var3);
                  var8.putInt(var4);

                  try {
                     var5.getModData().save(var8.bb);
                  } catch (IOException var10) {
                     var10.printStackTrace();
                  }

                  var7.endPacketImmediate();
               }
            }
         } catch (IOException var11) {
            var11.printStackTrace();
         }

      }
   }

   private static void receiveWeaponHit(ByteBuffer var0, UdpConnection var1) {
      IsoObject var2 = getIsoObjectRefFromByteBuffer(var0);
      short var3 = var0.getShort();
      String var4 = GameWindow.ReadStringUTF(var0);
      IsoPlayer var5 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var3));
      if (var2 != null && var5 != null) {
         InventoryItem var6 = null;
         if (!var4.isEmpty()) {
            var6 = InventoryItemFactory.CreateItem(var4);
            if (var6 == null || !(var6 instanceof HandWeapon)) {
               return;
            }
         }

         if (var6 == null && !(var2 instanceof IsoWindow)) {
            return;
         }

         int var7 = (int)var2.getX();
         int var8 = (int)var2.getY();
         int var9 = (int)var2.getZ();
         if (var2 instanceof IsoDoor) {
            ((IsoDoor)var2).WeaponHit(var5, (HandWeapon)var6);
         } else if (var2 instanceof IsoThumpable) {
            ((IsoThumpable)var2).WeaponHit(var5, (HandWeapon)var6);
         } else if (var2 instanceof IsoWindow) {
            ((IsoWindow)var2).WeaponHit(var5, (HandWeapon)var6);
         } else if (var2 instanceof IsoBarricade) {
            ((IsoBarricade)var2).WeaponHit(var5, (HandWeapon)var6);
         }

         if (var2.getObjectIndex() == -1) {
            ZLogger var10000 = LoggerManager.getLogger("map");
            String var10001 = var1.idStr;
            var10000.write(var10001 + " \"" + var1.username + "\" destroyed " + (var2.getName() != null ? var2.getName() : var2.getObjectName()) + " with " + (var4.isEmpty() ? "BareHands" : var4) + " at " + var7 + "," + var8 + "," + var9);
         }
      }

   }

   private static void putIsoObjectRefToByteBuffer(IsoObject var0, ByteBuffer var1) {
      var1.putInt(var0.square.x);
      var1.putInt(var0.square.y);
      var1.putInt(var0.square.z);
      var1.put((byte)var0.square.getObjects().indexOf(var0));
   }

   private static IsoObject getIsoObjectRefFromByteBuffer(ByteBuffer var0) {
      int var1 = var0.getInt();
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      byte var4 = var0.get();
      IsoGridSquare var5 = ServerMap.instance.getGridSquare(var1, var2, var3);
      return var5 != null && var4 >= 0 && var4 < var5.getObjects().size() ? (IsoObject)var5.getObjects().get(var4) : null;
   }

   private static void drink(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      float var3 = var0.getFloat();
      IsoPlayer var4 = getPlayerFromConnection(var1, var2);
      if (var4 != null) {
         Stats var10000 = var4.getStats();
         var10000.thirst -= var3;
         if (var4.getStats().thirst < 0.0F) {
            var4.getStats().thirst = 0.0F;
         }
      }

   }

   private static void receivePlayerDeath(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      float var3 = var0.getFloat();
      float var4 = var0.getFloat();
      float var5 = var0.getFloat();
      int var6 = var0.getInt();
      boolean var7 = var0.get() == 1;
      float var8 = var0.getFloat();
      IsoPlayer var9 = getPlayerFromConnection(var1, var2);
      if (var9 != null) {
         ChatServer.getInstance().disconnectPlayer(var9.getOnlineID());
         ServerWorldDatabase.instance.saveTransactionID(var9.username, 0);
         var9.setTransactionID(0);
         transactionIDMap.put(var9.username, 0);
         var9.setX(var3);
         var9.setY(var4);
         var9.setZ(var5);
         var9.setDir(var6);
         IsoGridSquare var10 = IsoWorld.instance.getCell().getGridSquare((double)var3, (double)var4, (double)var5);
         if (var10 != null) {
            var9.setCurrent(var10);
         }

         var9.setStateMachineLocked(false);
         var9.setHealth(0.0F);
         var9.getBodyDamage().setOverallBodyHealth(0.0F);
         var9.getBodyDamage().setInfected(var7);
         var9.getBodyDamage().setInfectionLevel(var8);
         var9.setStateMachineLocked(false);
         var9.setStateMachineLocked(true);
         if (!ServerOptions.instance.Open.getValue() && ServerOptions.instance.DropOffWhiteListAfterDeath.getValue() && var9.accessLevel.equals("")) {
            try {
               ServerWorldDatabase.instance.removeUser(var9.getUsername());
            } catch (SQLException var16) {
            }
         }

         try {
            var9.getInventory().clear();
            var9.getWornItems().clear();
            ArrayList var11 = var9.getInventory().load(var0, IsoWorld.getWorldVersion());
            byte var12 = var0.get();

            for(int var13 = 0; var13 < var12; ++var13) {
               String var14 = GameWindow.ReadString(var0);
               short var15 = var0.getShort();
               if (var15 >= 0 && var15 < var11.size() && var9.getWornItems().getBodyLocationGroup().getLocation(var14) != null) {
                  var9.getWornItems().setItem(var14, (InventoryItem)var11.get(var15));
               }
            }
         } catch (IOException var17) {
            var17.printStackTrace();
         }

         SendDeath(var1, var9);
         IsoDeadBody var18 = new IsoDeadBody(var9);
         if (var9.shouldBecomeZombieAfterDeath()) {
            var18.reanimateLater();
         }
      }

   }

   private static void receivePlayerOnBeaten(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      float var3 = var0.getFloat();
      float var4 = var0.getFloat();
      float var5 = var0.getFloat();
      IsoPlayer var6 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var6 != null) {
         SendOnBeaten(var6, var3, var4, var5);
      }

   }

   private static void process(ZomboidNetData var0) {
      ByteBuffer var1 = var0.buffer;
      UdpConnection var2 = udpEngine.getActiveConnection(var0.connection);

      try {
         switch(var0.type) {
         default:
            doZomboidDataInMainLoop(var0);
         }
      } catch (Exception var4) {
         DebugLog.log(DebugType.Network, "Error with packet of type: " + var0.type);
         var4.printStackTrace();
      }
   }

   private static void eatFood(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      float var3 = var0.getFloat();
      InventoryItem var4 = null;

      try {
         var4 = InventoryItem.loadItem(var0, 184);
      } catch (Exception var6) {
         var6.printStackTrace();
      }

      if (var4 instanceof Food) {
         IsoPlayer var5 = getPlayerFromConnection(var1, var2);
         if (var5 != null) {
            var5.Eat(var4, var3);
         }
      }

   }

   private static void doBandage(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var0.getInt();
         boolean var5 = var0.get() == 1;
         float var6 = var0.getFloat();
         boolean var7 = var0.get() == 1;
         String var8 = GameWindow.ReadStringUTF(var0);
         var3.getBodyDamage().SetBandaged(var4, var5, var6, var7, var8);

         for(int var9 = 0; var9 < udpEngine.connections.size(); ++var9) {
            UdpConnection var10 = (UdpConnection)udpEngine.connections.get(var9);
            if (var10.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var11 = var10.startPacket();
               PacketTypes.doPacket((short)42, var11);
               var11.putShort(var2);
               var11.putInt(var4);
               var11.putBoolean(var5);
               var11.putFloat(var6);
               var11.putBoolean(var7);
               GameWindow.WriteStringUTF(var11.bb, var8);
               var10.endPacketImmediate();
            }
         }
      }

   }

   private static void doStitch(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var0.getInt();
         boolean var5 = var0.get() == 1;
         float var6 = var0.getFloat();
         var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setStitched(var5);
         var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setStitchTime(var6);

         for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
            if (var8.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var9 = var8.startPacket();
               PacketTypes.doPacket((short)98, var9);
               var9.putShort(var2);
               var9.putInt(var4);
               var9.putBoolean(var5);
               var9.putFloat(var6);
               var8.endPacketImmediate();
            }
         }
      }

   }

   private static void doWoundInfection(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var0.getInt();
         boolean var5 = var0.get() == 1;
         var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setInfectedWound(var5);

         for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
            UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
            if (var7.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var8 = var7.startPacket();
               PacketTypes.doPacket((short)97, var8);
               var8.putShort(var2);
               var8.putInt(var4);
               var8.putBoolean(var5);
               var7.endPacketImmediate();
            }
         }
      }

   }

   private static void doDisinfect(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var0.getInt();
         float var5 = var0.getFloat();
         BodyPart var6 = var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4));
         var6.setAlcoholLevel(var6.getAlcoholLevel() + var5);

         for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
            if (var8.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var9 = var8.startPacket();
               PacketTypes.doPacket((short)99, var9);
               var9.putShort(var2);
               var9.putInt(var4);
               var9.putFloat(var5);
               var8.endPacketImmediate();
            }
         }
      }

   }

   private static void doSplint(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var0.getInt();
         boolean var5 = var0.get() == 1;
         String var6 = var5 ? GameWindow.ReadStringUTF(var0) : null;
         float var7 = var5 ? var0.getFloat() : 0.0F;
         BodyPart var8 = var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4));
         var8.setSplint(var5, var7);
         var8.setSplintItem(var6);

         for(int var9 = 0; var9 < udpEngine.connections.size(); ++var9) {
            UdpConnection var10 = (UdpConnection)udpEngine.connections.get(var9);
            if (var10.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var11 = var10.startPacket();
               PacketTypes.doPacket((short)102, var11);
               var11.putShort(var2);
               var11.putInt(var4);
               var11.putBoolean(var5);
               if (var5) {
                  var11.putUTF(var6);
                  var11.putFloat(var7);
               }

               var10.endPacketImmediate();
            }
         }
      }

   }

   private static void doAdditionalPain(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var0.getInt();
         float var5 = var0.getFloat();
         BodyPart var6 = var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4));
         var6.setAdditionalPain(var6.getAdditionalPain() + var5);

         for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
            if (var8.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var9 = var8.startPacket();
               PacketTypes.doPacket((short)100, var9);
               var9.putShort(var2);
               var9.putInt(var4);
               var9.putFloat(var5);
               var8.endPacketImmediate();
            }
         }
      }

   }

   private static void doRemoveGlass(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var0.getInt();
         var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setHaveGlass(false);

         for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
            UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
            if (var6.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var7 = var6.startPacket();
               PacketTypes.doPacket((short)101, var7);
               var7.putShort(var2);
               var7.putInt(var4);
               var6.endPacketImmediate();
            }
         }
      }

   }

   private static void doRemoveBullet(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var0.getInt();
         int var5 = var0.getInt();
         var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setHaveBullet(false, var5);

         for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
            UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
            if (var7.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var8 = var7.startPacket();
               PacketTypes.doPacket((short)103, var8);
               var8.putShort(var2);
               var8.putInt(var4);
               var8.putInt(var5);
               var7.endPacketImmediate();
            }
         }
      }

   }

   private static void doCleanBurn(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var0.getInt();
         var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setNeedBurnWash(false);

         for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
            UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
            if (var6.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var7 = var6.startPacket();
               PacketTypes.doPacket((short)104, var7);
               var7.putShort(var2);
               var7.putInt(var4);
               var6.endPacketImmediate();
            }
         }
      }

   }

   private static void receiveBodyDamageUpdate(ByteBuffer var0, UdpConnection var1) {
      BodyDamageSync.instance.serverPacket(var0);
   }

   private static void ReceiveCommand(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);
      String var3 = null;
      var3 = handleClientCommand(var2.substring(1), var1);
      if (var3 == null) {
         var3 = handleServerCommand(var2.substring(1), var1);
      }

      if (var3 == null) {
         var3 = "Unknown command " + var2;
      }

      if (!var2.substring(1).startsWith("roll") && !var2.substring(1).startsWith("card")) {
         ChatServer.getInstance().sendMessageToServerChat(var1, var3);
      } else {
         ChatServer.getInstance().sendMessageToServerChat(var1, var3);
      }

   }

   private static String handleClientCommand(String var0, UdpConnection var1) {
      if (var0 == null) {
         return null;
      } else {
         ArrayList var2 = new ArrayList();
         Matcher var3 = Pattern.compile("([^\"]\\S*|\".*?\")\\s*").matcher(var0);

         while(var3.find()) {
            var2.add(var3.group(1).replace("\"", ""));
         }

         int var4 = var2.size();
         String[] var5 = (String[])var2.toArray(new String[var4]);
         String var6 = var4 > 0 ? var5[0].toLowerCase() : "";
         String var10000;
         if (var6.equals("card")) {
            PlayWorldSoundServer("ChatDrawCard", false, getAnyPlayerFromConnection(var1).getCurrentSquare(), 0.0F, 3.0F, 1.0F, false);
            var10000 = var1.username;
            return var10000 + " drew " + ServerOptions.getRandomCard();
         } else if (var6.equals("roll")) {
            if (var4 != 2) {
               return (String)ServerOptions.clientOptionsList.get("roll");
            } else {
               boolean var13 = false;

               try {
                  int var14 = Integer.parseInt(var5[1]);
                  PlayWorldSoundServer("ChatRollDice", false, getAnyPlayerFromConnection(var1).getCurrentSquare(), 0.0F, 3.0F, 1.0F, false);
                  var10000 = var1.username;
                  return var10000 + " rolls a " + var14 + "-sided dice and obtains " + Rand.Next(var14);
               } catch (Exception var10) {
                  return (String)ServerOptions.clientOptionsList.get("roll");
               }
            }
         } else if (var6.equals("changepwd")) {
            if (var4 == 3) {
               String var12 = var5[1];
               String var8 = var5[2];

               try {
                  return ServerWorldDatabase.instance.changePwd(var1.username, var12.trim(), var8.trim());
               } catch (SQLException var11) {
                  var11.printStackTrace();
                  return "A SQL error occured";
               }
            } else {
               return (String)ServerOptions.clientOptionsList.get("changepwd");
            }
         } else if (var6.equals("dragons")) {
            return "Sorry, you don't have the required materials.";
         } else if (var6.equals("dance")) {
            return "Stop kidding me...";
         } else if (var6.equals("safehouse")) {
            if (var4 == 2 && var1 != null) {
               if (!ServerOptions.instance.PlayerSafehouse.getValue() && !ServerOptions.instance.AdminSafehouse.getValue()) {
                  return "Safehouses are disabled on this server.";
               } else if ("release".equals(var5[1])) {
                  SafeHouse var7 = SafeHouse.hasSafehouse(var1.username);
                  if (var7 == null) {
                     return "You don't own a safehouse.";
                  } else if (!ServerOptions.instance.PlayerSafehouse.getValue() && !"admin".equals(var1.accessLevel) && !"moderator".equals(var1.accessLevel)) {
                     return "Only admin or moderator may release safehouses";
                  } else {
                     var7.removeSafeHouse((IsoPlayer)null);
                     return "Safehouse released";
                  }
               } else {
                  return (String)ServerOptions.clientOptionsList.get("safehouse");
               }
            } else {
               return (String)ServerOptions.clientOptionsList.get("safehouse");
            }
         } else {
            return null;
         }
      }
   }

   public static void Chat(String var0, UdpConnection var1, boolean var2) {
      Chat(var0, var1, var2, (byte)-1);
   }

   public static void Chat(String var0, UdpConnection var1, boolean var2, byte var3) {
      IsoPlayer var4 = null;
      if (var1 != null) {
         var4 = getAnyPlayerFromConnection(var1);
         if (var4 == null) {
            return;
         }
      }

      if (var1 == null || !var1.accessLevel.equals("") && !var1.accessLevel.equals("Observer") && !var1.accessLevel.equals("GM") || !var0.startsWith("[SERVERMSG]")) {
         if (var4 != null && !var0.startsWith("[SERVERMSG]")) {
            if (var3 == 0) {
               var4.Say(var0);
            } else if (var3 == 1) {
               var4.SayWhisper(var0);
            } else if (var3 == 2) {
               var4.SayShout(var0);
            }
         }

         for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
            UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
            if (var1 == null || var1 != null && var6.getConnectedGUID() != var1.getConnectedGUID() && var4 != null && var6.ReleventTo(var4.x, var4.y)) {
               ByteBufferWriter var7 = var6.startPacket();
               PacketTypes.doPacket((short)186, var7);
               var7.putInt(var4 != null ? var4.OnlineID : -1);
               var7.putByte(var3);
               var7.putUTF(var0);
               var7.putByte((byte)(var2 ? 1 : 0));
               var6.endPacketImmediate();
            }
         }

         if (!var0.equals("ZzzZZZzzzz") && !ZomboidRadio.isStaticSound(var0)) {
            ZLogger var10000 = LoggerManager.getLogger("chat");
            String var10001 = var1 == null ? "" : var1.idStr + " \"" + var1.username;
            var10000.write(var10001 + "\": \"" + var0 + "\"");
         }

      }
   }

   private static void Chat(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      String var3 = GameWindow.ReadString(var0);
      if (var3.length() > 256) {
         var3 = var3.substring(0, 256);
      }

      Chat(var3, var1, true, var2);
   }

   public static void doZomboidDataInMainLoop(ZomboidNetData var0) {
      synchronized(MainLoopNetDataHighPrioritet) {
         MainLoopNetDataHighPrioritet.add(var0);
      }
   }

   private static void hitCharacter(ByteBuffer var0, UdpConnection var1) {
      HitPacket var2 = new HitPacket();
      var2.parse(var0);
      if (var2.check()) {
         DebugLog.log(DebugType.Combat, var2.getDescription());
         if (var2.objType == 1) {
            IsoZombie var3 = (IsoZombie)var2.zom;
            var3.bKnockedDown = (var2.zombieFlags & 1) != 0;
            var3.setFakeDead((var2.zombieFlags & 2) != 0);
            var3.setHitFromBehind((var2.zombieFlags & 4) != 0);
            var3.bStaggerBack = (var2.zombieFlags & 8) != 0;
            var3.setVariable("bKnifeDeath", (var2.zombieFlags & 16) != 0);
            var3.setFallOnFront((var2.zombieFlags & 32) != 0);
            var3.networkAI.extraUpdate();
            if (var2.helmetFall && !PersistentOutfits.instance.isHatFallen(var3)) {
               PersistentOutfits.instance.setFallenHat(var3, true);
               if (ServerGUI.isCreated()) {
                  PersistentOutfits.instance.removeFallenHat(var3.getPersistentOutfitID(), var3);
                  ModelManager.instance.ResetNextFrame(var3);
               }
            }

            if (var2.jawStabAttach) {
               var3.setAttachedItem("JawStab", var2.item);
               var3.setVariable("bKnifeDeath", true);
            }
         } else if (var2.objType == 2) {
         }

         IsoPlayer var8 = var2.player;

         for(int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
            UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var4);
            if (var5.getConnectedGUID() != var1.getConnectedGUID() && var5.ReleventTo(var8.x, var8.y)) {
               ByteBufferWriter var6 = var5.startPacket();
               PacketTypes.doPacket((short)26, var6);
               var2.write(var6);
               var5.endPacket();
            }
         }

         var2.player.useChargeDelta = var2.charge;
         if (var2.objType != 4) {
            IsoGameCharacter var9 = var2.zom;
            BaseVehicle var10 = var2.vehicle;
            if (var2.objType == 1) {
               IsoZombie var11 = (IsoZombie)var9;
               if (var11.getStateMachine().getCurrent() == ZombieOnGroundState.instance()) {
                  var11.setReanimateTimer((float)(Rand.Next(60) + 30));
               }

               if (var11.getStateMachine().getCurrent() == ZombieGetUpState.instance()) {
                  float var7 = 15.0F - var9.def.Frame;
                  if (var7 < 2.0F) {
                     var7 = 2.0F;
                  }

                  var9.def.Frame = var7;
                  var11.setReanimateTimer((float)(Rand.Next(60) + 30));
               }
            }

            if (var2.objType == 2) {
               ZLogger var10000 = LoggerManager.getLogger("pvp");
               String var10001 = var8.username;
               var10000.write("user " + var10001 + " " + LoggerManager.getPlayerCoords(var8) + " hit user " + ((IsoPlayer)var9).username + " " + LoggerManager.getPlayerCoords((IsoPlayer)var9) + " with " + var2.typeAsString);
            }

            if (var10 == null) {
               var9.setX(var2.tx);
               var9.setY(var2.ty);
               var9.setCloseKilled(var2.bCloseKilled);
               var8.isCrit = var2.isCrit;
               var8.bDoShove = var2.doShove;
               var8.setAimAtFloor(var2.isAimAtFloor);
               HandWeapon var12 = var8.bareHands;
               if (var2.item instanceof HandWeapon) {
                  var12 = (HandWeapon)var2.item;
               }

               if (var2.zombieHitReaction != null && !var2.zombieHitReaction.isEmpty()) {
                  var8.setVariable("ZombieHitReaction", var2.zombieHitReaction);
               }

               var9.Hit(var12, var8, var2.damageSplit, var2.bIgnoreDamage, var2.rangeDel, true);
               var9.setHitForce(var2.ohit);
               var9.getHitDir().x = var2.ohitx;
               var9.getHitDir().y = var2.ohity;
               if (var9 instanceof IsoZombie) {
                  ((IsoZombie)var9).bKnockedDown = (var2.zombieFlags & 1) != 0;
                  ((IsoZombie)var9).setFakeDead((var2.zombieFlags & 2) != 0);
                  ((IsoZombie)var9).setHitFromBehind((var2.zombieFlags & 4) != 0);
                  ((IsoZombie)var9).bStaggerBack = (var2.zombieFlags & 8) != 0;
                  ((IsoZombie)var9).setVariable("bKnifeDeath", (var2.zombieFlags & 16) != 0);
                  ((IsoZombie)var9).setFallOnFront((var2.zombieFlags & 32) != 0);
               }

               if (var9.hasAnimationPlayer() && var9.getAnimationPlayer().isReady() && !var9.getAnimationPlayer().isBoneTransformsNeedFirstFrame()) {
                  var9.getAnimationPlayer().setAngle(var2.angle);
               } else {
                  var9.getForwardDirection().setDirection(var2.angle);
               }

               if (var2.hitReaction != null && !var2.hitReaction.isEmpty()) {
                  var9.setHitReaction(var2.hitReaction);
               }

               if (var2.dead && var9.isAlive()) {
                  var9.setOnDeathDone(true);
                  if (var9 instanceof IsoZombie) {
                     ((IsoZombie)var9).DoZombieInventory();
                  }

                  var9.setHealth(0.0F);
                  LuaEventManager.triggerEvent("OnZombieDead", var9);
                  var9.DoDeath((HandWeapon)null, (IsoGameCharacter)null, false);
               }
            } else {
               var10.hitVehicle(var8, (HandWeapon)var2.item);
            }

         }
      }
   }

   private static void equip(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      byte var3 = var0.get();
      byte var4 = var0.get();
      InventoryItem var5 = null;
      IsoPlayer var6 = getPlayerFromConnection(var1, var2);
      if (var4 == 1) {
         try {
            var5 = InventoryItem.loadItem(var0, 184);
         } catch (Exception var14) {
            var14.printStackTrace();
         }

         if (var5 == null) {
            LoggerManager.getLogger("user").write(var1.idStr + " equipped unknown item type");
            return;
         }
      }

      if (var6 != null) {
         if (var5 != null) {
            var5.setContainer(var6.getInventory());
         }

         if (var3 == 0) {
            var6.setPrimaryHandItem(var5);
         } else {
            if (var4 == 2) {
               var5 = var6.getPrimaryHandItem();
            }

            var6.setSecondaryHandItem(var5);
         }

         try {
            if (var4 == 1 && var5 != null && var0.get() == 1) {
               var5.getVisual().load(var0, 184);
            }
         } catch (IOException var13) {
            var13.printStackTrace();
         }
      }

      if (var6 != null) {
         for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
            if (var8.getConnectedGUID() != var1.getConnectedGUID()) {
               IsoPlayer var9 = getAnyPlayerFromConnection(var8);
               if (var9 != null) {
                  ByteBufferWriter var10 = var8.startPacket();
                  PacketTypes.doPacket((short)25, var10);
                  var10.putShort((short)var6.OnlineID);
                  var10.putByte(var3);
                  var10.putByte(var4);
                  if (var4 == 1) {
                     try {
                        var5.saveWithSize(var10.bb, false);
                        if (var5.getVisual() != null) {
                           var10.bb.put((byte)1);
                           var5.getVisual().save(var10.bb);
                        } else {
                           var10.bb.put((byte)0);
                        }
                     } catch (IOException var12) {
                        var12.printStackTrace();
                     }
                  }

                  var8.endPacketImmediate();
               }
            }
         }

      }
   }

   private static void scoreboard(UdpConnection var0) {
      ByteBufferWriter var1 = var0.startPacket();
      PacketTypes.doPacket((short)50, var1);
      ArrayList var2 = new ArrayList();
      ArrayList var3 = new ArrayList();
      ArrayList var4 = new ArrayList();

      int var5;
      for(var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
         UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);

         for(int var7 = 0; var7 < 4; ++var7) {
            if (var6.usernames[var7] != null) {
               var2.add(var6.usernames[var7]);
               IsoPlayer var8 = getPlayerByRealUserName(var6.usernames[var7]);
               if (var8 != null) {
                  var3.add(var8.getDisplayName());
               } else {
                  String var9 = ServerWorldDatabase.instance.getDisplayName(var6.usernames[var7]);
                  var3.add(var9 == null ? var6.usernames[var7] : var9);
               }

               if (SteamUtils.isSteamModeEnabled()) {
                  var4.add(var6.steamID);
               }
            }
         }
      }

      var1.putInt(var2.size());

      for(var5 = 0; var5 < var2.size(); ++var5) {
         var1.putUTF((String)var2.get(var5));
         var1.putUTF((String)var3.get(var5));
         if (SteamUtils.isSteamModeEnabled()) {
            var1.putLong((Long)var4.get(var5));
         }
      }

      var0.endPacketImmediate();
   }

   private static void receiveWorldSound(ByteBuffer var0) {
      int var1 = var0.getInt();
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      boolean var6 = var0.get() == 1;
      float var7 = var0.getFloat();
      float var8 = var0.getFloat();
      boolean var9 = var0.get() == 1;
      DebugLog.log(DebugType.Sound, "worldsound: received at " + var1 + "," + var2 + "," + var3 + " radius=" + var4);
      WorldSoundManager.instance.addSound(var9, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   private static void receiveSound(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      boolean var6 = var0.get() == 1;
      if (var2 != null && !var2.isEmpty()) {
         int var7 = 70;
         GameSound var8 = GameSounds.getSound(var2);
         int var9;
         if (var8 != null) {
            for(var9 = 0; var9 < var8.clips.size(); ++var9) {
               GameSoundClip var10 = (GameSoundClip)var8.clips.get(var9);
               if (var10.hasMaxDistance()) {
                  var7 = Math.max(var7, (int)var10.distanceMax);
               }
            }
         }

         for(var9 = 0; var9 < udpEngine.connections.size(); ++var9) {
            UdpConnection var13 = (UdpConnection)udpEngine.connections.get(var9);
            if (var13.getConnectedGUID() != var1.getConnectedGUID() && var13.isFullyConnected()) {
               IsoPlayer var11 = getAnyPlayerFromConnection(var13);
               if (var11 != null && var13.RelevantTo((float)var3, (float)var4, (float)var7)) {
                  ByteBufferWriter var12 = var13.startPacket();
                  PacketTypes.doPacket((short)53, var12);
                  var12.putUTF(var2);
                  var12.putInt(var3);
                  var12.putInt(var4);
                  var12.putInt(var5);
                  var12.putByte((byte)(var6 ? 1 : 0));
                  var13.endPacketImmediate();
               }
            }
         }

      }
   }

   private static void PlayWorldSound(String var0, boolean var1, IsoGridSquare var2, float var3, float var4, float var5, boolean var6, boolean var7) {
      if (bServer && var2 != null) {
         int var8 = var2.getX();
         int var9 = var2.getY();
         int var10 = var2.getZ();
         DebugLog.log(DebugType.Sound, "sound: sending " + var0 + " at " + var8 + "," + var9 + "," + var10 + " radius=" + var4);

         for(int var11 = 0; var11 < udpEngine.connections.size(); ++var11) {
            UdpConnection var12 = (UdpConnection)udpEngine.connections.get(var11);
            IsoPlayer var13 = getAnyPlayerFromConnection(var12);
            if (var13 != null && var12.RelevantTo((float)var8, (float)var9, var4 * 2.0F)) {
               ByteBufferWriter var14 = var12.startPacket();
               PacketTypes.doPacket((short)53, var14);
               var14.putUTF(var0);
               var14.putInt(var8);
               var14.putInt(var9);
               var14.putInt(var10);
               var14.putByte((byte)(var1 ? 1 : 0));
               var14.putByte((byte)1);
               var12.endPacketImmediate();
            }
         }

      }
   }

   public static void PlayWorldSoundServer(String var0, boolean var1, IsoGridSquare var2, float var3, float var4, float var5, boolean var6) {
      PlayWorldSound(var0, var1, var2, var3, var4, var5, var6, false);
   }

   public static void PlayWorldSoundWavServer(String var0, boolean var1, IsoGridSquare var2, float var3, float var4, float var5, boolean var6) {
      PlayWorldSound(var0, var1, var2, var3, var4, var5, var6, true);
   }

   public static void PlaySoundAtEveryPlayer(String var0, int var1, int var2, int var3) {
      PlaySoundAtEveryPlayer(var0, var1, var2, var3, false);
   }

   public static void PlaySoundAtEveryPlayer(String var0) {
      PlaySoundAtEveryPlayer(var0, -1, -1, -1, true);
   }

   public static void PlaySoundAtEveryPlayer(String var0, int var1, int var2, int var3, boolean var4) {
      if (bServer) {
         if (var4) {
            DebugLog.log(DebugType.Sound, "sound: sending " + var0 + " at every player (using player location)");
         } else {
            DebugLog.log(DebugType.Sound, "sound: sending " + var0 + " at every player location x=" + var1 + " y=" + var2);
         }

         for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
            UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
            IsoPlayer var7 = getAnyPlayerFromConnection(var6);
            if (var7 != null && !var7.isDeaf()) {
               if (var4) {
                  var1 = (int)var7.getX();
                  var2 = (int)var7.getY();
                  var3 = (int)var7.getZ();
               }

               ByteBufferWriter var8 = var6.startPacket();
               PacketTypes.doPacket((short)119, var8);
               var8.putUTF(var0);
               var8.putInt(var1);
               var8.putInt(var2);
               var8.putInt(var3);
               var6.endPacketImmediate();
            }
         }

      }
   }

   public static void sendZombieSound(ZombieSound var0, IsoZombie var1) {
      float var2 = (float)var0.radius();
      DebugLog.log(DebugType.Sound, "sound: sending zombie sound " + var0);

      for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
         if (var4.isFullyConnected() && var4.RelevantTo(var1.getX(), var1.getY(), var2)) {
            ByteBufferWriter var5 = var4.startPacket();
            PacketTypes.doPacket((short)61, var5);
            var5.putShort(var1.OnlineID);
            var5.putByte((byte)var0.ordinal());
            var4.endPacketImmediate();
         }
      }

   }

   private static void receiveZombieHelmetFalling(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      short var3 = var0.getShort();
      String var4 = GameWindow.ReadString(var0);
      IsoZombie var5 = ServerMap.instance.ZombieMap.get(var3);
      IsoPlayer var6 = getPlayerFromConnection(var1, var2);
      if (var6 != null && var5 != null) {
         var5.serverRemoveItemFromZombie(var4);

         for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
            if (var8.getConnectedGUID() != var1.getConnectedGUID()) {
               IsoPlayer var9 = getAnyPlayerFromConnection(var1);
               if (var9 != null) {
                  try {
                     ByteBufferWriter var10 = var8.startPacket();
                     PacketTypes.doPacket((short)174, var10);
                     var10.putShort(var3);
                     var10.putUTF(var4);
                     var8.endPacketImmediate();
                  } catch (Throwable var11) {
                     var1.cancelPacket();
                     ExceptionLogger.logException(var11);
                  }
               }
            }
         }

      }
   }

   private static void receiveAttachedItem(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      String var3 = GameWindow.ReadString(var0);
      boolean var4 = var0.get() == 1;
      InventoryItem var5 = null;
      if (var4) {
         String var6 = GameWindow.ReadString(var0);
         var5 = InventoryItemFactory.CreateItem(var6);
         if (var5 == null) {
            return;
         }
      }

      IsoPlayer var12 = getPlayerFromConnection(var1, var2);
      if (var12 != null) {
         var12.setAttachedItem(var3, var5);

         for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
            if (var8.getConnectedGUID() != var1.getConnectedGUID()) {
               IsoPlayer var9 = getAnyPlayerFromConnection(var1);
               if (var9 != null) {
                  try {
                     ByteBufferWriter var10 = var8.startPacket();
                     PacketTypes.doPacket((short)173, var10);
                     var10.putShort((short)var12.OnlineID);
                     GameWindow.WriteString(var10.bb, var3);
                     var10.putByte((byte)(var4 ? 1 : 0));
                     if (var4) {
                        GameWindow.WriteString(var10.bb, var5.getFullType());
                     }

                     var8.endPacketImmediate();
                  } catch (Throwable var11) {
                     var1.cancelPacket();
                     ExceptionLogger.logException(var11);
                  }
               }
            }
         }

      }
   }

   private static void receiveClothing(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      String var3 = GameWindow.ReadString(var0);
      byte var4 = var0.get();
      InventoryItem var5 = null;
      if (var4 == 1) {
         try {
            var5 = InventoryItem.loadItem(var0, 184);
         } catch (Exception var15) {
            var15.printStackTrace();
         }

         if (var5 == null) {
            return;
         }
      }

      IsoPlayer var6 = getPlayerFromConnection(var1, var2);
      if (var6 != null) {
         try {
            var6.getHumanVisual().load(var0, 184);
            var6.getItemVisuals().load(var0, 184);
         } catch (Throwable var14) {
            ExceptionLogger.logException(var14);
            return;
         }

         if (var4 == 1) {
            var6.getWornItems().setItem(var3, var5);
         }

         for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
            if (var8.getConnectedGUID() != var1.getConnectedGUID()) {
               IsoPlayer var9 = getAnyPlayerFromConnection(var1);
               if (var9 != null) {
                  ByteBufferWriter var10 = var8.startPacket();
                  PacketTypes.doPacket((short)56, var10);

                  try {
                     var10.putShort((short)var6.OnlineID);
                     var10.putUTF(var3);
                     var10.putByte(var4);
                     if (var4 == 1) {
                        try {
                           var5.saveWithSize(var10.bb, false);
                        } catch (IOException var12) {
                           var12.printStackTrace();
                        }
                     }

                     var6.getHumanVisual().save(var10.bb);
                     ItemVisuals var11 = new ItemVisuals();
                     var6.getItemVisuals(var11);
                     var11.save(var10.bb);
                     var8.endPacketImmediate();
                  } catch (Throwable var13) {
                     var8.cancelPacket();
                     ExceptionLogger.logException(var13);
                  }
               }
            }
         }

         if (ServerGUI.isCreated()) {
            var6.resetModelNextFrame();
         }

      }
   }

   private static void receiveVisual(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      IsoPlayer var3 = getPlayerFromConnection(var1, var2);
      if (var3 != null) {
         try {
            var3.getHumanVisual().load(var0, 184);
         } catch (Throwable var10) {
            ExceptionLogger.logException(var10);
            return;
         }

         for(int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
            UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var4);
            if (var5.getConnectedGUID() != var1.getConnectedGUID()) {
               IsoPlayer var6 = getAnyPlayerFromConnection(var1);
               if (var6 != null) {
                  ByteBufferWriter var7 = var5.startPacket();
                  PacketTypes.doPacket((short)3, var7);

                  try {
                     var7.putShort((short)var3.OnlineID);
                     var3.getHumanVisual().save(var7.bb);
                     var5.endPacketImmediate();
                  } catch (Throwable var9) {
                     var5.cancelPacket();
                     ExceptionLogger.logException(var9);
                  }
               }
            }
         }

      }
   }

   public static void initClientCommandFilter() {
      String var0 = ServerOptions.getInstance().ClientCommandFilter.getValue();
      ccFilters.clear();
      String[] var1 = var0.split(";");
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         if (!var5.isEmpty() && var5.contains(".") && (var5.startsWith("+") || var5.startsWith("-"))) {
            String[] var6 = var5.split("\\.");
            if (var6.length == 2) {
               String var7 = var6[0].substring(1);
               String var8 = var6[1];
               zombie.network.GameServer.CCFilter var9 = new zombie.network.GameServer.CCFilter();
               var9.command = var8;
               var9.allow = var6[0].startsWith("+");
               var9.next = (zombie.network.GameServer.CCFilter)ccFilters.get(var7);
               ccFilters.put(var7, var9);
            }
         }
      }

   }

   private static void receiveClientCommand(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      String var3 = GameWindow.ReadString(var0);
      String var4 = GameWindow.ReadString(var0);
      boolean var5 = var0.get() == 1;
      KahluaTable var6 = null;
      if (var5) {
         var6 = LuaManager.platform.newTable();

         try {
            TableNetworkUtils.load(var6, var0);
         } catch (Exception var9) {
            var9.printStackTrace();
            return;
         }
      }

      IsoPlayer var7 = getPlayerFromConnection(var1, var2);
      if (var2 == -1) {
         var7 = getAnyPlayerFromConnection(var1);
      }

      if (var7 == null) {
         DebugLog.log("receiveClientCommand: player is null");
      } else {
         zombie.network.GameServer.CCFilter var8 = (zombie.network.GameServer.CCFilter)ccFilters.get(var3);
         if (var8 == null || var8.passes(var4)) {
            ZLogger var10000 = LoggerManager.getLogger("cmd");
            String var10001 = var1.idStr;
            var10000.write(var10001 + " \"" + var7.username + "\" " + var3 + "." + var4 + " @ " + (int)var7.getX() + "," + (int)var7.getY() + "," + (int)var7.getZ());
         }

         LuaEventManager.triggerEvent("OnClientCommand", var3, var4, var7, var6);
      }
   }

   private static void receiveGlobalObjects(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      IsoPlayer var3 = getPlayerFromConnection(var1, var2);
      if (var2 == -1) {
         var3 = getAnyPlayerFromConnection(var1);
      }

      if (var3 == null) {
         DebugLog.log("receiveGlobalObjects: player is null");
      } else {
         SGlobalObjectNetwork.receive(var0, var3);
      }
   }

   public static IsoPlayer getAnyPlayerFromConnection(UdpConnection var0) {
      for(int var1 = 0; var1 < 4; ++var1) {
         if (var0.players[var1] != null) {
            return var0.players[var1];
         }
      }

      return null;
   }

   private static IsoPlayer getPlayerFromConnection(UdpConnection var0, int var1) {
      return var1 >= 0 && var1 < 4 ? var0.players[var1] : null;
   }

   public static IsoPlayer getPlayerByRealUserName(String var0) {
      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);

         for(int var3 = 0; var3 < 4; ++var3) {
            IsoPlayer var4 = var2.players[var3];
            if (var4 != null && var4.username.equals(var0)) {
               return var4;
            }
         }
      }

      return null;
   }

   public static IsoPlayer getPlayerByUserName(String var0) {
      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);

         for(int var3 = 0; var3 < 4; ++var3) {
            IsoPlayer var4 = var2.players[var3];
            if (var4 != null && (var4.getDisplayName().equals(var0) || var4.getUsername().equals(var0))) {
               return var4;
            }
         }
      }

      return null;
   }

   public static IsoPlayer getPlayerByUserNameForCommand(String var0) {
      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);

         for(int var3 = 0; var3 < 4; ++var3) {
            IsoPlayer var4 = var2.players[var3];
            if (var4 != null && (var4.getDisplayName().toLowerCase().equals(var0.toLowerCase()) || var4.getDisplayName().toLowerCase().startsWith(var0.toLowerCase()))) {
               return var4;
            }
         }
      }

      return null;
   }

   public static UdpConnection getConnectionByPlayerOnlineID(Integer var0) {
      return udpEngine.getActiveConnection((Long)IDToAddressMap.get(var0));
   }

   public static UdpConnection getConnectionFromPlayer(IsoPlayer var0) {
      Long var1 = (Long)PlayerToAddressMap.get(var0);
      return var1 == null ? null : udpEngine.getActiveConnection(var1);
   }

   private static void removeBlood(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      boolean var5 = var0.get() == 1;
      IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var6 != null) {
         var6.removeBlood(false, var5);

         for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
            if (var8 != var1 && var8.ReleventTo((float)var2, (float)var3)) {
               ByteBufferWriter var9 = var8.startPacket();
               PacketTypes.doPacket((short)109, var9);
               var9.putInt(var2);
               var9.putInt(var3);
               var9.putInt(var4);
               var9.putBoolean(var5);
               var8.endPacketImmediate();
            }
         }

      }
   }

   public static void sendAddItemToContainer(ItemContainer var0, InventoryItem var1) {
      Object var2 = var0.getParent();
      if (var0.getContainingItem() != null && var0.getContainingItem().getWorldItem() != null) {
         var2 = var0.getContainingItem().getWorldItem();
      }

      IsoGridSquare var3 = ((IsoObject)var2).getSquare();

      for(int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
         UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var4);
         if (var5.ReleventTo((float)var3.x, (float)var3.y)) {
            ByteBufferWriter var6 = var5.startPacket();
            PacketTypes.doPacket((short)20, var6);
            if (var2 instanceof IsoDeadBody) {
               var6.putShort((short)0);
               var6.putInt(((IsoObject)var2).square.getX());
               var6.putInt(((IsoObject)var2).square.getY());
               var6.putInt(((IsoObject)var2).square.getZ());
               var6.putByte((byte)((IsoObject)var2).getStaticMovingObjectIndex());
            } else if (var2 instanceof IsoWorldInventoryObject) {
               var6.putShort((short)1);
               var6.putInt(((IsoObject)var2).square.getX());
               var6.putInt(((IsoObject)var2).square.getY());
               var6.putInt(((IsoObject)var2).square.getZ());
               var6.putInt(((IsoWorldInventoryObject)var2).getItem().id);
            } else if (var2 instanceof BaseVehicle) {
               var6.putShort((short)3);
               var6.putInt(((IsoObject)var2).square.getX());
               var6.putInt(((IsoObject)var2).square.getY());
               var6.putInt(((IsoObject)var2).square.getZ());
               var6.putShort(((BaseVehicle)var2).VehicleID);
               var6.putByte((byte)var0.vehiclePart.getIndex());
            } else {
               var6.putShort((short)2);
               var6.putInt(((IsoObject)var2).square.getX());
               var6.putInt(((IsoObject)var2).square.getY());
               var6.putInt(((IsoObject)var2).square.getZ());
               var6.putByte((byte)((IsoObject)var2).square.getObjects().indexOf(var2));
               var6.putByte((byte)((IsoObject)var2).getContainerIndex(var0));
            }

            try {
               CompressIdenticalItems.save(var6.bb, var1);
            } catch (Exception var8) {
               var8.printStackTrace();
            }

            var5.endPacket();
         }
      }

   }

   public static void sendRemoveItemFromContainer(ItemContainer var0, InventoryItem var1) {
      Object var2 = var0.getParent();
      if (var0.getContainingItem() != null && var0.getContainingItem().getWorldItem() != null) {
         var2 = var0.getContainingItem().getWorldItem();
      }

      if (var2 == null) {
         DebugLog.log("sendRemoveItemFromContainer: o is null");
      } else {
         IsoGridSquare var3 = ((IsoObject)var2).getSquare();
         if (var3 == null) {
            DebugLog.log("sendRemoveItemFromContainer: square is null");
         } else {
            for(int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
               UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var4);
               if (var5.ReleventTo((float)var3.x, (float)var3.y)) {
                  ByteBufferWriter var6 = var5.startPacket();
                  PacketTypes.doPacket((short)22, var6);
                  if (var2 instanceof IsoDeadBody) {
                     var6.putShort((short)0);
                     var6.putInt(((IsoObject)var2).square.getX());
                     var6.putInt(((IsoObject)var2).square.getY());
                     var6.putInt(((IsoObject)var2).square.getZ());
                     var6.putByte((byte)((IsoObject)var2).getStaticMovingObjectIndex());
                     var6.putInt(1);
                     var6.putInt(var1.id);
                  } else if (var2 instanceof IsoWorldInventoryObject) {
                     var6.putShort((short)1);
                     var6.putInt(((IsoObject)var2).square.getX());
                     var6.putInt(((IsoObject)var2).square.getY());
                     var6.putInt(((IsoObject)var2).square.getZ());
                     var6.putInt(((IsoWorldInventoryObject)var2).getItem().id);
                     var6.putInt(1);
                     var6.putInt(var1.id);
                  } else if (var2 instanceof BaseVehicle) {
                     var6.putShort((short)3);
                     var6.putInt(((IsoObject)var2).square.getX());
                     var6.putInt(((IsoObject)var2).square.getY());
                     var6.putInt(((IsoObject)var2).square.getZ());
                     var6.putShort(((BaseVehicle)var2).VehicleID);
                     var6.putByte((byte)var0.vehiclePart.getIndex());
                     var6.putInt(1);
                     var6.putInt(var1.id);
                  } else {
                     var6.putShort((short)2);
                     var6.putInt(((IsoObject)var2).square.getX());
                     var6.putInt(((IsoObject)var2).square.getY());
                     var6.putInt(((IsoObject)var2).square.getZ());
                     var6.putByte((byte)((IsoObject)var2).square.getObjects().indexOf(var2));
                     var6.putByte((byte)((IsoObject)var2).getContainerIndex(var0));
                     var6.putInt(1);
                     var6.putInt(var1.id);
                  }

                  var5.endPacketImmediate();
               }
            }

         }
      }
   }

   private static void removeItemFromContainer(ByteBuffer var0, UdpConnection var1) {
      alreadyRemoved.clear();
      ByteBufferReader var2 = new ByteBufferReader(var0);
      short var3 = var2.getShort();
      int var4 = var2.getInt();
      int var5 = var2.getInt();
      int var6 = var2.getInt();
      IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare(var4, var5, var6);
      if (var7 == null) {
         var7 = ServerMap.instance.getGridSquare(var4, var5, var6);
      }

      HashSet var8 = new HashSet();
      boolean var9 = false;
      int var10 = 0;
      byte var11;
      int var14;
      if (var3 == 0) {
         var11 = var2.getByte();
         var10 = var0.getInt();
         if (var7 != null && var11 >= 0 && var11 < var7.getStaticMovingObjects().size()) {
            IsoObject var12 = (IsoObject)var7.getStaticMovingObjects().get(var11);
            if (var12 != null && var12.getContainer() != null) {
               for(int var13 = 0; var13 < var10; ++var13) {
                  var14 = var2.getInt();
                  InventoryItem var15 = var12.getContainer().getItemWithID(var14);
                  if (var15 == null) {
                     alreadyRemoved.add(var14);
                  } else {
                     var12.getContainer().Remove(var15);
                     var9 = true;
                     var8.add(var15.getFullType());
                  }
               }

               var12.getContainer().setExplored(true);
               var12.getContainer().setHasBeenLooted(true);
            }
         }
      } else {
         int var32;
         if (var3 == 1) {
            if (var7 != null) {
               long var19 = var2.getLong();
               var10 = var0.getInt();
               ItemContainer var26 = null;

               for(var14 = 0; var14 < var7.getWorldObjects().size(); ++var14) {
                  IsoWorldInventoryObject var29 = (IsoWorldInventoryObject)var7.getWorldObjects().get(var14);
                  if (var29 != null && var29.getItem() instanceof InventoryContainer && (long)var29.getItem().id == var19) {
                     var26 = ((InventoryContainer)var29.getItem()).getInventory();
                     break;
                  }
               }

               if (var26 != null) {
                  for(var14 = 0; var14 < var10; ++var14) {
                     var32 = var2.getInt();
                     InventoryItem var16 = var26.getItemWithID(var32);
                     if (var16 == null) {
                        alreadyRemoved.add(var32);
                     } else {
                        var26.Remove(var16);
                        var8.add(var16.getFullType());
                     }
                  }

                  var26.setExplored(true);
                  var26.setHasBeenLooted(true);
               }
            }
         } else {
            byte var22;
            int var34;
            if (var3 == 2) {
               var11 = var2.getByte();
               var22 = var2.getByte();
               var10 = var0.getInt();
               if (var7 != null && var11 >= 0 && var11 < var7.getObjects().size()) {
                  IsoObject var27 = (IsoObject)var7.getObjects().get(var11);
                  ItemContainer var31 = var27 != null ? var27.getContainerByIndex(var22) : null;
                  if (var31 != null) {
                     for(var32 = 0; var32 < var10; ++var32) {
                        var34 = var2.getInt();
                        InventoryItem var17 = var31.getItemWithID(var34);
                        if (var17 == null) {
                           alreadyRemoved.add(var34);
                        } else {
                           var31.Remove(var17);
                           var31.setExplored(true);
                           var31.setHasBeenLooted(true);
                           var9 = true;
                           var8.add(var17.getFullType());
                        }
                     }

                     LuaManager.updateOverlaySprite(var27);
                  }
               }
            } else if (var3 == 3) {
               short var20 = var2.getShort();
               var22 = var2.getByte();
               var10 = var0.getInt();
               BaseVehicle var28 = VehicleManager.instance.getVehicleByID(var20);
               if (var28 != null) {
                  VehiclePart var33 = var28 == null ? null : var28.getPartByIndex(var22);
                  ItemContainer var35 = var33 == null ? null : var33.getItemContainer();
                  if (var35 != null) {
                     for(var34 = 0; var34 < var10; ++var34) {
                        int var36 = var2.getInt();
                        InventoryItem var18 = var35.getItemWithID(var36);
                        if (var18 == null) {
                           alreadyRemoved.add(var36);
                        } else {
                           var35.Remove(var18);
                           var35.setExplored(true);
                           var35.setHasBeenLooted(true);
                           var9 = true;
                           var8.add(var18.getFullType());
                        }
                     }
                  }
               }
            }
         }
      }

      for(int var21 = 0; var21 < udpEngine.connections.size(); ++var21) {
         UdpConnection var23 = (UdpConnection)udpEngine.connections.get(var21);
         if (var23.getConnectedGUID() != var1.getConnectedGUID() && var7 != null && var23.ReleventTo((float)var7.x, (float)var7.y)) {
            var0.rewind();
            ByteBufferWriter var30 = var23.startPacket();
            PacketTypes.doPacket((short)22, var30);
            var30.bb.put(var0);
            var23.endPacketUnordered();
         }
      }

      if (!alreadyRemoved.isEmpty()) {
         ByteBufferWriter var24 = var1.startPacket();
         PacketTypes.doPacket((short)49, var24);
         var24.putInt(alreadyRemoved.size());

         for(int var25 = 0; var25 < alreadyRemoved.size(); ++var25) {
            var24.putLong((long)(Integer)alreadyRemoved.get(var25));
         }

         var1.endPacketUnordered();
      }

      alreadyRemoved.clear();
      LoggerManager.getLogger("item").write(var1.idStr + " \"" + var1.username + "\" container -" + var10 + " " + var4 + "," + var5 + "," + var6 + " " + var8.toString());
   }

   private static void readItemStats(ByteBuffer var0, InventoryItem var1) {
      int var2 = var0.getInt();
      float var3 = var0.getFloat();
      boolean var4 = var0.get() == 1;
      var1.setUses(var2);
      if (var1 instanceof DrainableComboItem) {
         ((DrainableComboItem)var1).setDelta(var3);
         ((DrainableComboItem)var1).updateWeight();
      }

      if (var4 && var1 instanceof Food) {
         Food var5 = (Food)var1;
         var5.setHungChange(var0.getFloat());
         var5.setCalories(var0.getFloat());
         var5.setCarbohydrates(var0.getFloat());
         var5.setLipids(var0.getFloat());
         var5.setProteins(var0.getFloat());
         var5.setThirstChange(var0.getFloat());
         var5.setFluReduction(var0.getInt());
         var5.setPainReduction(var0.getFloat());
         var5.setEndChange(var0.getFloat());
         var5.setReduceFoodSickness(var0.getInt());
         var5.setStressChange(var0.getFloat());
         var5.setFatigueChange(var0.getFloat());
      }

   }

   private static void receiveItemStats(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare(var3, var4, var5);
      byte var8;
      int var9;
      byte var14;
      int var15;
      int var16;
      ItemContainer var22;
      InventoryItem var24;
      switch(var2) {
      case 0:
         var14 = var0.get();
         var16 = var0.getInt();
         if (var6 != null && var14 >= 0 && var14 < var6.getStaticMovingObjects().size()) {
            IsoMovingObject var19 = (IsoMovingObject)var6.getStaticMovingObjects().get(var14);
            var22 = var19.getContainer();
            if (var22 != null) {
               var24 = var22.getItemWithID(var16);
               if (var24 != null) {
                  readItemStats(var0, var24);
               }
            }
         }
         break;
      case 1:
         var15 = var0.getInt();
         if (var6 != null) {
            for(var16 = 0; var16 < var6.getWorldObjects().size(); ++var16) {
               IsoWorldInventoryObject var18 = (IsoWorldInventoryObject)var6.getWorldObjects().get(var16);
               if (var18.getItem() != null && var18.getItem().id == var15) {
                  readItemStats(var0, var18.getItem());
                  break;
               }

               if (var18.getItem() instanceof InventoryContainer) {
                  var22 = ((InventoryContainer)var18.getItem()).getInventory();
                  var24 = var22.getItemWithID(var15);
                  if (var24 != null) {
                     readItemStats(var0, var24);
                     break;
                  }
               }
            }
         }
         break;
      case 2:
         var14 = var0.get();
         var8 = var0.get();
         var9 = var0.getInt();
         if (var6 != null && var14 >= 0 && var14 < var6.getObjects().size()) {
            IsoObject var20 = (IsoObject)var6.getObjects().get(var14);
            ItemContainer var23 = var20.getContainerByIndex(var8);
            if (var23 != null) {
               InventoryItem var25 = var23.getItemWithID(var9);
               if (var25 != null) {
                  readItemStats(var0, var25);
               }
            }
         }
         break;
      case 3:
         short var7 = var0.getShort();
         var8 = var0.get();
         var9 = var0.getInt();
         BaseVehicle var10 = VehicleManager.instance.getVehicleByID(var7);
         if (var10 != null) {
            VehiclePart var11 = var10.getPartByIndex(var8);
            if (var11 != null) {
               ItemContainer var12 = var11.getItemContainer();
               if (var12 != null) {
                  InventoryItem var13 = var12.getItemWithID(var9);
                  if (var13 != null) {
                     readItemStats(var0, var13);
                  }
               }
            }
         }
      }

      for(var15 = 0; var15 < udpEngine.connections.size(); ++var15) {
         UdpConnection var17 = (UdpConnection)udpEngine.connections.get(var15);
         if (var17 != var1 && var17.ReleventTo((float)var3, (float)var4)) {
            ByteBufferWriter var21 = var17.startPacket();
            PacketTypes.doPacket((short)35, var21);
            var0.rewind();
            var21.bb.put(var0);
            var17.endPacket();
         }
      }

   }

   private static void requestItemsForContainer(ByteBuffer var0, UdpConnection var1) {
      ByteBufferReader var2 = new ByteBufferReader(var0);
      short var3 = var0.getShort();
      String var4 = GameWindow.ReadString(var0);
      String var5 = GameWindow.ReadString(var0);
      int var6 = var2.getInt();
      int var7 = var2.getInt();
      int var8 = var2.getInt();
      short var9 = var2.getShort();
      byte var10 = -1;
      byte var11 = -1;
      int var12 = 0;
      short var13 = 0;
      IsoGridSquare var14 = IsoWorld.instance.CurrentCell.getGridSquare(var6, var7, var8);
      IsoObject var15 = null;
      ItemContainer var16 = null;
      int var24;
      if (var9 == 2) {
         var10 = var2.getByte();
         var11 = var2.getByte();
         if (var14 != null && var10 >= 0 && var10 < var14.getObjects().size()) {
            var15 = (IsoObject)var14.getObjects().get(var10);
            if (var15 != null) {
               var16 = var15.getContainerByIndex(var11);
               if (var16 == null || var16.isExplored()) {
                  return;
               }
            }
         }
      } else if (var9 == 3) {
         var13 = var2.getShort();
         var11 = var2.getByte();
         BaseVehicle var23 = VehicleManager.instance.getVehicleByID(var13);
         if (var23 != null) {
            VehiclePart var17 = ((BaseVehicle)var23).getPartByIndex(var11);
            var16 = var17 == null ? null : var17.getItemContainer();
            if (var16 == null || var16.isExplored()) {
               return;
            }
         }
      } else if (var9 == 1) {
         var12 = var2.getInt();

         for(var24 = 0; var24 < var14.getWorldObjects().size(); ++var24) {
            IsoWorldInventoryObject var18 = (IsoWorldInventoryObject)var14.getWorldObjects().get(var24);
            if (var18 != null && var18.getItem() instanceof InventoryContainer && var18.getItem().id == var12) {
               var16 = ((InventoryContainer)var18.getItem()).getInventory();
               break;
            }
         }
      } else if (var9 == 0) {
         var10 = var2.getByte();
         if (var14 != null && var10 >= 0 && var10 < var14.getStaticMovingObjects().size()) {
            var15 = (IsoObject)var14.getStaticMovingObjects().get(var10);
            if (var15 != null && var15.getContainer() != null) {
               if (var15.getContainer().isExplored()) {
                  return;
               }

               var16 = var15.getContainer();
            }
         }
      }

      if (var16 != null && !var16.isExplored()) {
         var16.setExplored(true);
         var24 = var16.Items.size();
         ItemPickerJava.fillContainer(var16, (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var3)));
         if (var24 != var16.Items.size()) {
            for(int var25 = 0; var25 < udpEngine.connections.size(); ++var25) {
               UdpConnection var19 = (UdpConnection)udpEngine.connections.get(var25);
               if (var19.ReleventTo((float)var14.x, (float)var14.y)) {
                  ByteBufferWriter var20 = var19.startPacket();
                  PacketTypes.doPacket((short)20, var20);
                  var20.putShort(var9);
                  var20.putInt(var6);
                  var20.putInt(var7);
                  var20.putInt(var8);
                  if (var9 == 0) {
                     var20.putByte(var10);
                  } else if (var9 == 1) {
                     var20.putInt(var12);
                  } else if (var9 == 3) {
                     var20.putShort(var13);
                     var20.putByte(var11);
                  } else {
                     var20.putByte(var10);
                     var20.putByte(var11);
                  }

                  try {
                     CompressIdenticalItems.save(var20.bb, var16.getItems(), (IsoGameCharacter)null);
                  } catch (Exception var22) {
                     var22.printStackTrace();
                  }

                  var19.endPacketUnordered();
               }
            }

         }
      }
   }

   public static void sendItemsInContainer(IsoObject var0, ItemContainer var1) {
      if (udpEngine != null) {
         if (var1 == null) {
            DebugLog.log("sendItemsInContainer: container is null");
         } else {
            if (var0 instanceof IsoWorldInventoryObject) {
               IsoWorldInventoryObject var2 = (IsoWorldInventoryObject)var0;
               if (!(var2.getItem() instanceof InventoryContainer)) {
                  DebugLog.log("sendItemsInContainer: IsoWorldInventoryObject item isn't a container");
                  return;
               }

               InventoryContainer var3 = (InventoryContainer)var2.getItem();
               if (var3.getInventory() != var1) {
                  DebugLog.log("sendItemsInContainer: wrong container for IsoWorldInventoryObject");
                  return;
               }
            } else if (var0 instanceof BaseVehicle) {
               if (var1.vehiclePart == null || var1.vehiclePart.getItemContainer() != var1 || var1.vehiclePart.getVehicle() != var0) {
                  DebugLog.log("sendItemsInContainer: wrong container for BaseVehicle");
                  return;
               }
            } else if (var0 instanceof IsoDeadBody) {
               if (var1 != var0.getContainer()) {
                  DebugLog.log("sendItemsInContainer: wrong container for IsoDeadBody");
                  return;
               }
            } else if (var0.getContainerIndex(var1) == -1) {
               DebugLog.log("sendItemsInContainer: wrong container for IsoObject");
               return;
            }

            if (var0 != null && var1 != null && !var1.getItems().isEmpty()) {
               for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
                  UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
                  if (var8.ReleventTo((float)var0.square.x, (float)var0.square.y)) {
                     ByteBufferWriter var4 = var8.startPacket();
                     PacketTypes.doPacket((short)20, var4);
                     if (var0 instanceof IsoDeadBody) {
                        var4.putShort((short)0);
                     } else if (var0 instanceof IsoWorldInventoryObject) {
                        var4.putShort((short)1);
                     } else if (var0 instanceof BaseVehicle) {
                        var4.putShort((short)3);
                     } else {
                        var4.putShort((short)2);
                     }

                     var4.putInt(var0.getSquare().getX());
                     var4.putInt(var0.getSquare().getY());
                     var4.putInt(var0.getSquare().getZ());
                     if (var0 instanceof IsoDeadBody) {
                        var4.putByte((byte)var0.getStaticMovingObjectIndex());
                     } else if (var0 instanceof IsoWorldInventoryObject) {
                        var4.putLong((long)((IsoWorldInventoryObject)var0).getItem().id);
                     } else if (var0 instanceof BaseVehicle) {
                        var4.putShort(((BaseVehicle)var0).VehicleID);
                        var4.putByte((byte)var1.vehiclePart.getIndex());
                     } else {
                        var4.putByte((byte)var0.getObjectIndex());
                        var4.putByte((byte)var0.getContainerIndex(var1));
                     }

                     try {
                        CompressIdenticalItems.save(var4.bb, var1.getItems(), (IsoGameCharacter)null);
                     } catch (Exception var6) {
                        var6.printStackTrace();
                     }

                     var8.endPacketImmediate();
                  }
               }

            }
         }
      }
   }

   private static void logDupeItem(UdpConnection var0) {
      IsoPlayer var1 = null;

      for(int var2 = 0; var2 < Players.size(); ++var2) {
         if (var0.username.equals(((IsoPlayer)Players.get(var2)).username)) {
            var1 = (IsoPlayer)Players.get(var2);
            break;
         }
      }

      String var3 = "";
      if (var1 != null) {
         var3 = LoggerManager.getPlayerCoords(var1);
      }

      ZLogger var10000 = LoggerManager.getLogger("user");
      String var10001 = var1.getDisplayName();
      var10000.write("Error: Dupe item ID for " + var10001 + " " + var3);
      ServerWorldDatabase.instance.addUserlog(var0.username, UserlogType.DupeItem, "", "server", 1);
   }

   private static void sendItemsToContainer(ByteBuffer var0, UdpConnection var1) {
      ByteBufferReader var2 = new ByteBufferReader(var0);
      short var3 = var2.getShort();
      int var4 = var2.getInt();
      int var5 = var2.getInt();
      int var6 = var2.getInt();
      IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare(var4, var5, var6);
      HashSet var8 = new HashSet();
      byte var9 = 0;
      if (var7 == null) {
         DebugLog.log("ERROR sendItemsToContainer square is null");
      } else {
         ItemContainer var10 = null;
         IsoObject var11 = null;
         byte var12;
         int var20;
         if (var3 == 0) {
            var12 = var2.getByte();
            if (var12 < 0 || var12 >= var7.getStaticMovingObjects().size()) {
               DebugLog.log("ERROR sendItemsToContainer invalid corpse index");
               return;
            }

            IsoObject var13 = (IsoObject)var7.getStaticMovingObjects().get(var12);
            if (var13 != null && var13.getContainer() != null) {
               var10 = var13.getContainer();
            }
         } else if (var3 == 1) {
            int var19 = var2.getInt();

            for(var20 = 0; var20 < var7.getWorldObjects().size(); ++var20) {
               IsoWorldInventoryObject var14 = (IsoWorldInventoryObject)var7.getWorldObjects().get(var20);
               if (var14 != null && var14.getItem() instanceof InventoryContainer && var14.getItem().id == var19) {
                  var10 = ((InventoryContainer)var14.getItem()).getInventory();
                  break;
               }
            }

            if (var10 == null) {
               DebugLog.log("ERROR sendItemsToContainer can't find world item with id=" + var19);
               return;
            }
         } else {
            byte var21;
            if (var3 == 2) {
               var12 = var2.getByte();
               var21 = var2.getByte();
               if (var12 < 0 || var12 >= var7.getObjects().size()) {
                  DebugLog.log("ERROR sendItemsToContainer invalid object index");

                  for(int var23 = 0; var23 < var7.getObjects().size(); ++var23) {
                     if (((IsoObject)var7.getObjects().get(var23)).getContainer() != null) {
                        var12 = (byte)var23;
                        var21 = 0;
                        break;
                     }
                  }

                  if (var12 == -1) {
                     return;
                  }
               }

               var11 = (IsoObject)var7.getObjects().get(var12);
               var10 = var11 != null ? var11.getContainerByIndex(var21) : null;
            } else if (var3 == 3) {
               short var22 = var2.getShort();
               var21 = var2.getByte();
               BaseVehicle var26 = VehicleManager.instance.getVehicleByID(var22);
               if (var26 == null) {
                  DebugLog.log("ERROR sendItemsToContainer invalid vehicle id");
                  return;
               }

               VehiclePart var15 = var26.getPartByIndex(var21);
               var10 = var15 == null ? null : var15.getItemContainer();
            }
         }

         if (var10 != null) {
            try {
               ArrayList var24 = CompressIdenticalItems.load(var2.bb, 184, (ArrayList)null, (ArrayList)null);

               for(var20 = 0; var20 < var24.size(); ++var20) {
                  InventoryItem var27 = (InventoryItem)var24.get(var20);
                  if (var27 != null) {
                     if (var10.containsID(var27.id)) {
                        System.out.println("Error: Dupe item ID for " + var1.username);
                        logDupeItem(var1);
                     } else {
                        var10.addItem(var27);
                        var10.setExplored(true);
                        var8.add(var27.getFullType());
                        if (var11 instanceof IsoMannequin) {
                           ((IsoMannequin)var11).wearItem(var27, (IsoGameCharacter)null);
                        }
                     }
                  }
               }
            } catch (Exception var16) {
               var16.printStackTrace();
            }

            if (var11 != null) {
               LuaManager.updateOverlaySprite(var11);
               if ("campfire".equals(var10.getType())) {
                  var11.sendObjectChange("container.customTemperature");
               }
            }
         }
      }

      for(int var17 = 0; var17 < udpEngine.connections.size(); ++var17) {
         UdpConnection var18 = (UdpConnection)udpEngine.connections.get(var17);
         if (var18.getConnectedGUID() != var1.getConnectedGUID() && var18.ReleventTo((float)var7.x, (float)var7.y)) {
            var0.rewind();
            ByteBufferWriter var25 = var18.startPacket();
            PacketTypes.doPacket((short)20, var25);
            var25.bb.put(var0);
            var18.endPacketUnordered();
         }
      }

      LoggerManager.getLogger("item").write(var1.idStr + " \"" + var1.username + "\" container +" + var9 + " " + var4 + "," + var5 + "," + var6 + " " + var8.toString());
   }

   public static boolean CheckPlayerStillValid(IsoPlayer var0) {
      long var1 = (Long)PlayerToAddressMap.get(var0);
      return var0.getBodyDamage().getHealth() >= 0.0F;
   }

   public static void addConnection(UdpConnection var0) {
      synchronized(MainLoopNetDataHighPrioritet) {
         MainLoopNetDataHighPrioritet.add(new zombie.network.GameServer.DelayedConnection(var0, true));
      }
   }

   public static void addDisconnect(UdpConnection var0) {
      synchronized(MainLoopNetDataHighPrioritet) {
         MainLoopNetDataHighPrioritet.add(new zombie.network.GameServer.DelayedConnection(var0, false));
      }
   }

   public static void disconnectPlayer(IsoPlayer var0, UdpConnection var1) {
      if (var0 != null) {
         ChatServer.getInstance().disconnectPlayer(var0.getOnlineID());
         int var2;
         if (var0.getVehicle() != null) {
            VehiclesDB2.instance.updateVehicleAndTrailer(var0.getVehicle());
            if (var0.getVehicle().getDriver() == var0) {
               var0.getVehicle().netPlayerAuthorization = 0;
               var0.getVehicle().netPlayerId = -1;
               var0.getVehicle().getController().clientForce = 0.0F;
               var0.getVehicle().netLinearVelocity.set(0.0F, 0.0F, 0.0F);
            }

            var2 = var0.getVehicle().getSeat(var0);
            if (var2 != -1) {
               var0.getVehicle().clearPassenger(var2);
            }
         }

         if (!var0.isDead()) {
            ServerWorldDatabase.instance.saveTransactionID(var0.username, var0.getTransactionID());
         }

         var0.removeFromWorld();
         var0.removeFromSquare();
         PlayerToAddressMap.remove(var0);
         IDToAddressMap.remove(var0.OnlineID);
         IDToPlayerMap.remove(var0.OnlineID);
         Players.remove(var0);
         var1.usernames[var0.PlayerIndex] = null;
         var1.players[var0.PlayerIndex] = null;
         var1.playerIDs[var0.PlayerIndex] = -1;
         var1.ReleventPos[var0.PlayerIndex] = null;
         var1.connectArea[var0.PlayerIndex] = null;

         for(var2 = 0; var2 < udpEngine.connections.size(); ++var2) {
            UdpConnection var3 = (UdpConnection)udpEngine.connections.get(var2);
            ByteBufferWriter var4 = var3.startPacket();
            PacketTypes.doPacket((short)13, var4);
            var4.putInt(var0.OnlineID);
            var3.endPacketImmediate();
         }

         ServerLOS.instance.removePlayer(var0);
         ZombiePopulationManager.instance.updateLoadedAreas();
         DebugType var10000 = DebugType.Network;
         String var10001 = var0.getDisplayName();
         DebugLog.log(var10000, "Disconnected player \"" + var10001 + "\" " + var1.idStr);
         ZLogger var5 = LoggerManager.getLogger("user");
         var10001 = var1.idStr;
         var5.write(var10001 + " \"" + var0.getUsername() + "\" disconnected player " + LoggerManager.getPlayerCoords(var0));
      }
   }

   public static void heartBeat() {
      ++count;
   }

   public static int getFreeSlot() {
      for(int var0 = 0; var0 < udpEngine.getMaxConnections(); ++var0) {
         if (SlotToConnection[var0] == null) {
            return var0;
         }
      }

      return -1;
   }

   public static void receiveClientConnect(UdpConnection var0, LogonResult var1) {
      int var2 = getFreeSlot();
      int var3 = var2 * 4;
      if (var0.playerDownloadServer != null) {
         try {
            IDToAddressMap.put(var3, var0.getConnectedGUID());
            var0.playerDownloadServer.destroy();
         } catch (Exception var17) {
            var17.printStackTrace();
         }
      }

      playerToCoordsMap.put(var3, new Vector2());
      playerMovedToFastMap.put(var3, 0);
      SlotToConnection[var2] = var0;
      var0.playerIDs[0] = var3;
      IDToAddressMap.put(var3, var0.getConnectedGUID());
      var0.playerDownloadServer = new PlayerDownloadServer(var0, DEFAULT_PORT + var2 + 1);
      DebugLog.log(DebugType.Network, "Connected new client " + var0.username + " ID # " + var3 + " and assigned DL port " + var0.playerDownloadServer.port);
      var0.playerDownloadServer.startConnectionTest();
      KahluaTable var4 = SpawnPoints.instance.getSpawnRegions();

      for(int var5 = 1; var5 < var4.size() + 1; ++var5) {
         ByteBufferWriter var6 = var0.startPacket();
         PacketTypes.doPacket((short)171, var6);
         var6.putInt(var5);

         try {
            ((KahluaTable)var4.rawget(var5)).save(var6.bb);
            var0.endPacketImmediate();
         } catch (IOException var16) {
            var16.printStackTrace();
         }
      }

      VehicleManager.serverSendVehiclesConfig(var0);
      ByteBufferWriter var18 = var0.startPacket();
      PacketTypes.doPacket((short)21, var18);
      if (SteamUtils.isSteamModeEnabled() && CoopSlave.instance != null && !var0.isCoopHost) {
         var18.putByte((byte)1);
         var18.putLong(CoopSlave.instance.hostSteamID);
         var18.putUTF(ServerName);
      } else {
         var18.putByte((byte)0);
      }

      var18.putByte((byte)var2);
      var18.putInt(var0.playerDownloadServer.port);
      var18.putBoolean(UseTCPForMapDownloads);
      var18.putUTF(var1.accessLevel);
      var18.putUTF(GameMap);
      if (SteamUtils.isSteamModeEnabled()) {
         var18.putShort((short)WorkshopItems.size());

         for(int var19 = 0; var19 < WorkshopItems.size(); ++var19) {
            var18.putLong((Long)WorkshopItems.get(var19));
            var18.putLong(WorkshopTimeStamps[var19]);
         }
      }

      ArrayList var20 = new ArrayList();
      Mod var7 = null;

      Iterator var8;
      for(var8 = ServerMods.iterator(); var8.hasNext(); var20.add(var7)) {
         String var9 = (String)var8.next();
         String var10 = ZomboidFileSystem.instance.getModDir(var9);
         if (var10 != null) {
            try {
               var7 = ChooseGameInfo.readModInfo(var10);
            } catch (Exception var15) {
               ExceptionLogger.logException(var15);
               var7 = new Mod(var9);
               var7.setId(var9);
               var7.setName(var9);
            }
         } else {
            var7 = new Mod(var9);
            var7.setId(var9);
            var7.setName(var9);
         }
      }

      var18.putInt(var20.size());
      var8 = var20.iterator();

      while(var8.hasNext()) {
         Mod var22 = (Mod)var8.next();
         var18.putUTF(var22.getId());
         var18.putUTF(var22.getUrl());
         var18.putUTF(var22.getName());
      }

      Vector3 var21 = ServerMap.instance.getStartLocation(var1);
      var1.x = (int)var21.x;
      var1.y = (int)var21.y;
      var1.z = (int)var21.z;
      var18.putInt(var1.x);
      var18.putInt(var1.y);
      var18.putInt(var1.z);
      var18.putInt(ServerOptions.instance.getPublicOptions().size());
      var8 = null;
      Iterator var24 = ServerOptions.instance.getPublicOptions().iterator();

      while(var24.hasNext()) {
         String var23 = (String)var24.next();
         var18.putUTF(var23);
         var18.putUTF(ServerOptions.instance.getOption(var23));
      }

      try {
         SandboxOptions.instance.save(var18.bb);
         GameTime.getInstance().saveToPacket(var18.bb);
      } catch (IOException var14) {
         var14.printStackTrace();
      }

      ErosionMain.getInstance().getConfig().save(var18.bb);

      try {
         SGlobalObjects.saveInitialStateForClient(var18.bb);
      } catch (Throwable var13) {
         var13.printStackTrace();
      }

      var18.putInt(ResetID);
      GameWindow.WriteString(var18.bb, Core.getInstance().getPoisonousBerry());
      GameWindow.WriteString(var18.bb, Core.getInstance().getPoisonousMushroom());
      var18.putBoolean(var0.isCoopHost);

      try {
         WorldDictionary.saveDataForClient(var18.bb);
      } catch (Exception var12) {
         var12.printStackTrace();
      }

      var0.endPacketImmediate();
      if (!SteamUtils.isSteamModeEnabled()) {
         PublicServerUtil.updatePlayers();
      }

   }

   private static void sendLargeFile(UdpConnection var0, String var1) {
      int var2 = large_file_bb.position();

      int var4;
      for(int var3 = 0; var3 < var2; var3 += var4) {
         var4 = Math.min(1000, var2 - var3);
         ByteBufferWriter var5 = var0.startPacket();
         PacketTypes.doPacket((short)37, var5);
         var5.putUTF(var1);
         var5.putInt(var2);
         var5.putInt(var3);
         var5.putInt(var4);
         var5.bb.put(large_file_bb.array(), var3, var4);
         var0.endPacketImmediate();
      }

   }

   private static void receiveRequestData(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);
      ByteBufferWriter var3;
      if ("descriptors.bin".equals(var2)) {
         var3 = var1.startPacket();
         PacketTypes.doPacket((short)37, var3);
         var3.putUTF(var2);

         try {
            PersistentOutfits.instance.save(var3.bb);
         } catch (Exception var12) {
            var12.printStackTrace();
         }

         var1.endPacketImmediate();
      }

      if ("playerzombiedesc".equals(var2)) {
         var3 = var1.startPacket();
         PacketTypes.doPacket((short)37, var3);
         var3.putUTF(var2);
         Descriptor[] var4 = SharedDescriptors.getPlayerZombieDescriptors();
         int var5 = 0;

         for(int var6 = 0; var6 < var4.length; ++var6) {
            if (var4[var6] != null) {
               ++var5;
            }
         }

         try {
            var3.putShort((short)var5);
            Descriptor[] var14 = var4;
            int var7 = var4.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Descriptor var9 = var14[var8];
               if (var9 != null) {
                  var9.save(var3.bb);
               }
            }
         } catch (Exception var13) {
            var13.printStackTrace();
         }

         var1.endPacketImmediate();
      }

      if ("map_meta.bin".equals(var2)) {
         try {
            large_file_bb.clear();
            IsoWorld.instance.MetaGrid.savePart(large_file_bb, 0, true);
            IsoWorld.instance.MetaGrid.savePart(large_file_bb, 1, true);
            sendLargeFile(var1, var2);
         } catch (Exception var11) {
            var11.printStackTrace();
            var3 = var1.startPacket();
            PacketTypes.doPacket((short)83, var3);
            var3.putUTF("You have been kicked from this server because map_meta.bin could not be saved.");
            var1.endPacketImmediate();
            var1.forceDisconnect();
            addDisconnect(var1);
         }
      }

      if ("map_zone.bin".equals(var2)) {
         try {
            large_file_bb.clear();
            IsoWorld.instance.MetaGrid.saveZone(large_file_bb);
            sendLargeFile(var1, var2);
         } catch (Exception var10) {
            var10.printStackTrace();
            var3 = var1.startPacket();
            PacketTypes.doPacket((short)83, var3);
            var3.putUTF("You have been kicked from this server because map_zone.bin could not be saved.");
            var1.endPacketImmediate();
            var1.forceDisconnect();
            addDisconnect(var1);
         }
      }

   }

   public static void sendMetaGrid(int var0, int var1, int var2, UdpConnection var3) {
      IsoMetaGrid var4 = IsoWorld.instance.MetaGrid;
      if (var0 >= var4.getMinX() && var0 <= var4.getMaxX() && var1 >= var4.getMinY() && var1 <= var4.getMaxY()) {
         IsoMetaCell var5 = var4.getCellData(var0, var1);
         if (var5.info != null && var2 >= 0 && var2 < var5.info.RoomList.size()) {
            ByteBufferWriter var6 = var3.startPacket();
            PacketTypes.doPacket((short)9, var6);
            var6.putShort((short)var0);
            var6.putShort((short)var1);
            var6.putShort((short)var2);
            var6.putBoolean(var5.info.getRoom(var2).def.bLightsActive);
            var3.endPacketImmediate();
         }
      }
   }

   public static void sendMetaGrid(int var0, int var1, int var2) {
      for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
         sendMetaGrid(var0, var1, var2, var4);
      }

   }

   private static void preventIndoorZombies(int var0, int var1, int var2) {
      RoomDef var3 = IsoWorld.instance.MetaGrid.getRoomAt(var0, var1, var2);
      if (var3 != null) {
         boolean var4 = isSpawnBuilding(var3.getBuilding());
         var3.getBuilding().setAllExplored(true);
         ArrayList var5 = IsoWorld.instance.CurrentCell.getZombieList();

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            IsoZombie var7 = (IsoZombie)var5.get(var6);
            if ((var4 || var7.bIndoorZombie) && var7.getSquare() != null && var7.getSquare().getRoom() != null && var7.getSquare().getRoom().def.building == var3.getBuilding()) {
               VirtualZombieManager.instance.removeZombieFromWorld(var7);
               if (var6 >= var5.size() || var5.get(var6) != var7) {
                  --var6;
               }
            }
         }

      }
   }

   private static void receivePlayerConnect(ByteBuffer var0, UdpConnection var1, String var2) {
      byte var3 = var0.get();
      if (var3 >= 0 && var3 < 4 && var1.players[var3] == null) {
         byte var4 = var0.get();
         var1.ReleventRange = (byte)(var4 / 2 + 2);
         float var5 = var0.getFloat();
         float var6 = var0.getFloat();
         float var7 = var0.getFloat();
         var1.ReleventPos[var3].x = var5;
         var1.ReleventPos[var3].y = var6;
         var1.ReleventPos[var3].z = var7;
         var1.connectArea[var3] = null;
         var1.ChunkGridWidth = var4;
         var1.loadedCells[var3] = new ClientServerMap(var3, (int)var5, (int)var6, var4);
         SurvivorDesc var8 = SurvivorFactory.CreateSurvivor();

         try {
            var8.load(var0, 184, (IsoGameCharacter)null);
         } catch (IOException var22) {
            var22.printStackTrace();
         }

         IsoPlayer var9 = new IsoPlayer((IsoCell)null, var8, (int)var5, (int)var6, (int)var7);
         var9.PlayerIndex = var3;
         var9.OnlineChunkGridWidth = var4;
         Players.add(var9);
         var9.bRemote = true;

         try {
            var9.getHumanVisual().load(var0, 184);
            var9.getItemVisuals().load(var0, 184);
         } catch (IOException var21) {
            var21.printStackTrace();
         }

         int var10 = var1.playerIDs[var3];
         IDToPlayerMap.put(var10, var9);
         var1.players[var3] = var9;
         PlayerToAddressMap.put(var9, var1.getConnectedGUID());
         var9.OnlineID = var10;

         try {
            var9.getXp().load(var0, 184);
         } catch (IOException var20) {
            var20.printStackTrace();
         }

         var9.setAllChatMuted(var0.get() == 1);
         var1.allChatMuted = var9.isAllChatMuted();
         var9.setTagPrefix(GameWindow.ReadString(var0));
         var9.setTagColor(new ColorInfo(var0.getFloat(), var0.getFloat(), var0.getFloat(), 1.0F));
         var9.setTransactionID(var0.getInt());
         var9.setHoursSurvived(var0.getDouble());
         var9.setZombieKills(var0.getInt());
         var9.setDisplayName(GameWindow.ReadString(var0));
         var9.setSpeakColour(new Color(var0.getFloat(), var0.getFloat(), var0.getFloat(), 1.0F));
         var9.showTag = var0.get() == 1;
         var9.factionPvp = var0.get() == 1;
         if (SteamUtils.isSteamModeEnabled()) {
            var9.setSteamID(var1.steamID);
            String var11 = GameWindow.ReadStringUTF(var0);
            SteamGameServer.BUpdateUserData(var1.steamID, var1.username, 0);
         }

         byte var23 = var0.get();
         InventoryItem var12 = null;
         if (var23 == 1) {
            try {
               var12 = InventoryItem.loadItem(var0, 184);
            } catch (IOException var19) {
               var19.printStackTrace();
               return;
            }

            if (var12 == null) {
               LoggerManager.getLogger("user").write(var1.idStr + " equipped unknown item");
               return;
            }

            var9.setPrimaryHandItem(var12);
         }

         var12 = null;
         byte var13 = var0.get();
         if (var13 == 2) {
            var9.setSecondaryHandItem(var9.getPrimaryHandItem());
         }

         if (var13 == 1) {
            try {
               var12 = InventoryItem.loadItem(var0, 184);
            } catch (IOException var18) {
               var18.printStackTrace();
               return;
            }

            if (var12 == null) {
               LoggerManager.getLogger("user").write(var1.idStr + " equipped unknown item");
               return;
            }

            var9.setSecondaryHandItem(var12);
         }

         int var14 = var0.getInt();

         int var15;
         for(var15 = 0; var15 < var14; ++var15) {
            String var16 = GameWindow.ReadString(var0);
            InventoryItem var17 = InventoryItemFactory.CreateItem(GameWindow.ReadString(var0));
            if (var17 != null) {
               var9.setAttachedItem(var16, var17);
            }
         }

         var15 = var0.getInt();
         var9.remoteSneakLvl = var15;
         var9.username = var2;
         var9.accessLevel = var1.accessLevel;
         if (!var9.accessLevel.equals("") && CoopSlave.instance == null) {
            var9.setGhostMode(true);
            var9.setInvisible(true);
            var9.setGodMod(true);
         }

         ChatServer.getInstance().initPlayer(var9.OnlineID);
         var1.setFullyConnected();
         sendWeather(var1);

         for(int var24 = 0; var24 < udpEngine.connections.size(); ++var24) {
            UdpConnection var26 = (UdpConnection)udpEngine.connections.get(var24);
            sendPlayerConnect(var9, var26);
         }

         Iterator var25 = IDToPlayerMap.values().iterator();

         while(var25.hasNext()) {
            IsoPlayer var27 = (IsoPlayer)var25.next();
            if (var27.getOnlineID() != var9.getOnlineID()) {
               sendPlayerConnect(var27, var1);
            }
         }

         var1.loadedCells[var3].setLoaded();
         var1.loadedCells[var3].sendPacket(var1);
         preventIndoorZombies((int)var5, (int)var6, (int)var7);
         ServerLOS.instance.addPlayer(var9);
         ZLogger var10000 = LoggerManager.getLogger("user");
         String var10001 = var1.idStr;
         var10000.write(var10001 + " \"" + var9.username + "\" fully connected " + LoggerManager.getPlayerCoords(var9));
      }
   }

   private static void receivePlayerSave(ByteBuffer var0, UdpConnection var1) {
      if ((Calendar.getInstance().getTimeInMillis() - previousSave) / 60000L >= 0L) {
         byte var2 = var0.get();
         if (var2 >= 0 && var2 < 4) {
            int var3 = var0.getInt();
            float var4 = var0.getFloat();
            float var5 = var0.getFloat();
            float var6 = var0.getFloat();
            ServerMap.instance.saveZoneInsidePlayerInfluence(var3);
         }
      }
   }

   private static void receiveSendPlayerProfile(ByteBuffer var0, UdpConnection var1) {
      ServerPlayerDB.getInstance().serverUpdateNetworkCharacter(var0, var1);
   }

   private static void receiveLoadPlayerProfile(ByteBuffer var0, UdpConnection var1) {
      ServerPlayerDB.getInstance().serverLoadNetworkCharacter(var0, var1);
   }

   private static void coopAccessGranted(int var0, UdpConnection var1) {
      ByteBufferWriter var2 = var1.startPacket();
      PacketTypes.doPacket((short)27, var2);
      var2.putBoolean(true);
      var2.putByte((byte)var0);
      var1.endPacketImmediate();
   }

   private static void coopAccessDenied(String var0, int var1, UdpConnection var2) {
      ByteBufferWriter var3 = var2.startPacket();
      PacketTypes.doPacket((short)27, var3);
      var3.putBoolean(false);
      var3.putByte((byte)var1);
      var3.putUTF(var0);
      var2.endPacketImmediate();
   }

   private static void addCoopPlayer(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      byte var3 = var0.get();
      if (var3 >= 0 && var3 < 4) {
         if (var1.players[var3] != null && !var1.players[var3].isDead()) {
            coopAccessDenied("Coop player " + (var3 + 1) + "/4 already exists", var3, var1);
         } else {
            String var4;
            if (var2 != 1) {
               if (var2 == 2) {
                  var4 = var1.usernames[var3];
                  if (var4 == null) {
                     coopAccessDenied("Coop player login wasn't received", var3, var1);
                  } else {
                     DebugLog.log("coop player=" + (var3 + 1) + "/4 username=\"" + var4 + "\" player info received");
                     receivePlayerConnect(var0, var1, var4);
                  }
               }
            } else {
               var4 = GameWindow.ReadStringUTF(var0);
               if (var4.isEmpty()) {
                  coopAccessDenied("No username given", var3, var1);
               } else {
                  int var5;
                  for(var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
                     UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);

                     for(int var7 = 0; var7 < 4; ++var7) {
                        if ((var6 != var1 || var3 != var7) && var4.equals(var6.usernames[var7])) {
                           coopAccessDenied("User \"" + var4 + "\" already connected", var3, var1);
                           return;
                        }
                     }
                  }

                  DebugLog.log("coop player=" + (var3 + 1) + "/4 username=\"" + var4 + "\" is joining");
                  float var11;
                  if (var1.players[var3] != null) {
                     DebugLog.log("coop player=" + (var3 + 1) + "/4 username=\"" + var4 + "\" is replacing dead player");
                     var5 = var1.players[var3].OnlineID;
                     disconnectPlayer(var1.players[var3], var1);
                     float var10 = var0.getFloat();
                     var11 = var0.getFloat();
                     var1.usernames[var3] = var4;
                     var1.ReleventPos[var3] = new Vector3(var10, var11, 0.0F);
                     var1.connectArea[var3] = new Vector3(var10 / 10.0F, var11 / 10.0F, (float)var1.ChunkGridWidth);
                     var1.playerIDs[var3] = var5;
                     IDToAddressMap.put(var5, var1.getConnectedGUID());
                     coopAccessGranted(var3, var1);
                     ZombiePopulationManager.instance.updateLoadedAreas();
                     if (ChatServer.isInited()) {
                        ChatServer.getInstance().initPlayer(var5);
                     }

                  } else if (getPlayerCount() >= ServerOptions.instance.MaxPlayers.getValue()) {
                     coopAccessDenied("Server is full", var3, var1);
                  } else {
                     var5 = -1;

                     int var9;
                     for(var9 = 0; var9 < udpEngine.getMaxConnections(); ++var9) {
                        if (SlotToConnection[var9] == var1) {
                           var5 = var9;
                           break;
                        }
                     }

                     var9 = var5 * 4 + var3;
                     DebugLog.log("coop player=" + (var3 + 1) + "/4 username=\"" + var4 + "\" assigned id=" + var9);
                     var11 = var0.getFloat();
                     float var8 = var0.getFloat();
                     var1.usernames[var3] = var4;
                     var1.ReleventPos[var3] = new Vector3(var11, var8, 0.0F);
                     var1.playerIDs[var3] = var9;
                     var1.connectArea[var3] = new Vector3(var11 / 10.0F, var8 / 10.0F, (float)var1.ChunkGridWidth);
                     IDToAddressMap.put(var9, var1.getConnectedGUID());
                     coopAccessGranted(var3, var1);
                     ZombiePopulationManager.instance.updateLoadedAreas();
                  }
               }
            }
         }
      } else {
         coopAccessDenied("Invalid coop player index", var3, var1);
      }
   }

   private static void sendInitialWorldState(UdpConnection var0) {
      if (RainManager.isRaining()) {
         sendStartRain(var0);
      }

      try {
         ClimateManager.getInstance().sendInitialState(var0);
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   private static void receiveObjectModData(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      boolean var6 = var0.get() == 1;
      IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var7 != null && var5 >= 0 && var5 < var7.getObjects().size()) {
         IsoObject var8 = (IsoObject)var7.getObjects().get(var5);
         int var9;
         if (var6) {
            var9 = var8.getWaterAmount();

            try {
               var8.getModData().load(var0, 184);
            } catch (IOException var11) {
               var11.printStackTrace();
            }

            if (var9 != var8.getWaterAmount()) {
               LuaEventManager.triggerEvent("OnWaterAmountChange", var8, var9);
            }
         } else if (var8.hasModData()) {
            var8.getModData().wipe();
         }

         for(var9 = 0; var9 < udpEngine.connections.size(); ++var9) {
            UdpConnection var10 = (UdpConnection)udpEngine.connections.get(var9);
            if (var10.getConnectedGUID() != var1.getConnectedGUID() && var10.ReleventTo((float)var2, (float)var3)) {
               sendObjectModData(var8, var10);
            }
         }
      } else if (var7 != null) {
         DebugLog.log("receiveObjectModData: index=" + var5 + " is invalid x,y,z=" + var2 + "," + var3 + "," + var4);
      } else if (bDebug) {
         DebugLog.log("receiveObjectModData: sq is null x,y,z=" + var2 + "," + var3 + "," + var4);
      }

   }

   private static void sendObjectModData(IsoObject var0, UdpConnection var1) {
      if (var0.getSquare() != null) {
         ByteBufferWriter var2 = var1.startPacket();
         PacketTypes.doPacket((short)58, var2);
         var2.putInt(var0.getSquare().getX());
         var2.putInt(var0.getSquare().getY());
         var2.putInt(var0.getSquare().getZ());
         var2.putInt(var0.getSquare().getObjects().indexOf(var0));
         if (var0.getModData().isEmpty()) {
            var2.putByte((byte)0);
         } else {
            var2.putByte((byte)1);

            try {
               var0.getModData().save(var2.bb);
            } catch (IOException var4) {
               var4.printStackTrace();
            }
         }

         var1.endPacketImmediate();
      }
   }

   public static void sendObjectModData(IsoObject var0) {
      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);
         if (var2.ReleventTo(var0.getX(), var0.getY())) {
            sendObjectModData(var0, var2);
         }
      }

   }

   public static void sendSlowFactor(IsoGameCharacter var0) {
      if (var0 instanceof IsoPlayer) {
         if (PlayerToAddressMap.containsKey(var0)) {
            long var1 = (Long)PlayerToAddressMap.get((IsoPlayer)var0);
            UdpConnection var3 = udpEngine.getActiveConnection(var1);
            if (var3 != null) {
               ByteBufferWriter var4 = var3.startPacket();
               PacketTypes.doPacket((short)63, var4);
               var4.putByte((byte)((IsoPlayer)var0).PlayerIndex);
               var4.putFloat(var0.getSlowTimer());
               var4.putFloat(var0.getSlowFactor());
               var3.endPacketImmediate();
            }
         }
      }
   }

   private static void sendObjectChange(IsoObject var0, String var1, KahluaTable var2, UdpConnection var3) {
      if (var0.getSquare() != null) {
         ByteBufferWriter var4 = var3.startPacket();
         PacketTypes.doPacket((short)59, var4);
         if (var0 instanceof IsoPlayer) {
            var4.putByte((byte)1);
            var4.putInt(((IsoPlayer)var0).OnlineID);
         } else if (var0 instanceof BaseVehicle) {
            var4.putByte((byte)2);
            var4.putShort(((BaseVehicle)var0).getId());
         } else if (var0 instanceof IsoWorldInventoryObject) {
            var4.putByte((byte)3);
            var4.putInt(var0.getSquare().getX());
            var4.putInt(var0.getSquare().getY());
            var4.putInt(var0.getSquare().getZ());
            var4.putInt(((IsoWorldInventoryObject)var0).getItem().getID());
         } else {
            var4.putByte((byte)0);
            var4.putInt(var0.getSquare().getX());
            var4.putInt(var0.getSquare().getY());
            var4.putInt(var0.getSquare().getZ());
            var4.putInt(var0.getSquare().getObjects().indexOf(var0));
         }

         var4.putUTF(var1);
         var0.saveChange(var1, var2, var4.bb);
         var3.endPacketImmediate();
      }
   }

   public static void sendObjectChange(IsoObject var0, String var1, KahluaTable var2) {
      if (var0 != null) {
         for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
            UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
            if (var4.ReleventTo(var0.getX(), var0.getY())) {
               sendObjectChange(var0, var1, var2, var4);
            }
         }

      }
   }

   public static void sendObjectChange(IsoObject var0, String var1, Object... var2) {
      if (var2.length == 0) {
         sendObjectChange(var0, var1, (KahluaTable)null);
      } else if (var2.length % 2 == 0) {
         KahluaTable var3 = LuaManager.platform.newTable();

         for(int var4 = 0; var4 < var2.length; var4 += 2) {
            Object var5 = var2[var4 + 1];
            if (var5 instanceof Float) {
               var3.rawset(var2[var4], ((Float)var5).doubleValue());
            } else if (var5 instanceof Integer) {
               var3.rawset(var2[var4], ((Integer)var5).doubleValue());
            } else if (var5 instanceof Short) {
               var3.rawset(var2[var4], ((Short)var5).doubleValue());
            } else {
               var3.rawset(var2[var4], var5);
            }
         }

         sendObjectChange(var0, var1, var3);
      }
   }

   private static void updateHandEquips(UdpConnection var0, IsoPlayer var1) {
      ByteBufferWriter var2 = var0.startPacket();
      PacketTypes.doPacket((short)25, var2);
      var2.putShort((short)var1.OnlineID);
      var2.putByte((byte)0);
      var2.putByte((byte)(var1.getPrimaryHandItem() != null ? 1 : 0));
      if (var1.getPrimaryHandItem() != null) {
         try {
            var1.getPrimaryHandItem().saveWithSize(var2.bb, false);
            if (var1.getPrimaryHandItem().getVisual() != null) {
               var2.bb.put((byte)1);
               var1.getPrimaryHandItem().getVisual().save(var2.bb);
            } else {
               var2.bb.put((byte)0);
            }
         } catch (IOException var5) {
            var5.printStackTrace();
         }
      }

      var0.endPacketImmediate();
      var2 = var0.startPacket();
      PacketTypes.doPacket((short)25, var2);
      var2.putShort((short)var1.OnlineID);
      var2.putByte((byte)1);
      if (var1.getSecondaryHandItem() == var1.getPrimaryHandItem() && var1.getSecondaryHandItem() != null) {
         var2.putByte((byte)2);
      } else {
         var2.putByte((byte)(var1.getSecondaryHandItem() != null ? 1 : 0));
      }

      if (var1.getSecondaryHandItem() != null) {
         try {
            var1.getSecondaryHandItem().saveWithSize(var2.bb, false);
            if (var1.getSecondaryHandItem().getVisual() != null) {
               var2.bb.put((byte)1);
               var1.getSecondaryHandItem().getVisual().save(var2.bb);
            } else {
               var2.bb.put((byte)0);
            }
         } catch (IOException var4) {
            var4.printStackTrace();
         }
      }

      var0.endPacketImmediate();
   }

   public static void sendZombie(IsoZombie var0) {
      if (!bFastForward) {
         ;
      }
   }

   public static void sendZombieUpdate(IsoZombie var0, UdpConnection var1, int var2) {
      if (var1 != null && var1.ReleventTo(var0.x, var0.y)) {
         ByteBufferWriter var3 = var1.startPacket();
         PacketTypes.doPacket((short)10, var3);
         var3.putShort((short)1);
         ZombieUpdateInfoPacket.writeZombie(var3, var0, var2);
         var1.endPacketImmediate();
      }

   }

   public static void sendZombieUpdate(IsoZombie var0) {
      if (var0 != null && var0.OnlineID != -1) {
         Iterator var1 = PlayerToAddressMap.entrySet().iterator();

         while(var1.hasNext()) {
            Entry var2 = (Entry)var1.next();
            if (var2.getValue() != null && var2.getKey() != null && ((IsoPlayer)var2.getKey()).getOnlineID() != -1) {
               UdpConnection var3 = udpEngine.getActiveConnection((Long)var2.getValue());
               sendZombieUpdate(var0, var3, ((IsoPlayer)var2.getKey()).getOnlineID());
            }
         }
      }

   }

   public static void SyncCustomLightSwitchSettings(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      byte var5 = var0.get();
      IsoGridSquare var6 = ServerMap.instance.getGridSquare(var2, var3, var4);
      if (var6 != null && var5 >= 0 && var5 < var6.getObjects().size()) {
         if (var6.getObjects().get(var5) instanceof IsoLightSwitch) {
            ((IsoLightSwitch)var6.getObjects().get(var5)).receiveSyncCustomizedSettings(var0, var1);
         } else {
            DebugLog.log("Sync Lightswitch custom settings: found object not a instance of IsoLightSwitch, x,y,z=" + var2 + "," + var3 + "," + var4);
         }
      } else if (var6 != null) {
         DebugLog.log("Sync Lightswitch custom settings: index=" + var5 + " is invalid x,y,z=" + var2 + "," + var3 + "," + var4);
      } else {
         DebugLog.log("Sync Lightswitch custom settings: sq is null x,y,z=" + var2 + "," + var3 + "," + var4);
      }

   }

   private static void sendAlarmClock_Player(short var0, long var1, boolean var3, int var4, int var5, boolean var6, UdpConnection var7) {
      ByteBufferWriter var8 = var7.startPacket();
      PacketTypes.doPacket((short)46, var8);
      var8.putShort(AlarmClock.PacketPlayer);
      var8.putShort(var0);
      var8.putLong(var1);
      var8.putByte((byte)(var3 ? 1 : 0));
      if (!var3) {
         var8.putInt(var4);
         var8.putInt(var5);
         var8.putByte((byte)(var6 ? 1 : 0));
      }

      var7.endPacket();
   }

   private static void sendAlarmClock_World(int var0, int var1, int var2, long var3, boolean var5, int var6, int var7, boolean var8, UdpConnection var9) {
      ByteBufferWriter var10 = var9.startPacket();
      PacketTypes.doPacket((short)46, var10);
      var10.putShort(AlarmClock.PacketWorld);
      var10.putInt(var0);
      var10.putInt(var1);
      var10.putInt(var2);
      var10.putLong(var3);
      var10.putByte((byte)(var5 ? 1 : 0));
      if (!var5) {
         var10.putInt(var6);
         var10.putInt(var7);
         var10.putByte((byte)(var8 ? 1 : 0));
      }

      var9.endPacket();
   }

   private static void SyncAlarmClock(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      int var4;
      int var6;
      int var9;
      if (var2 == AlarmClock.PacketPlayer) {
         short var15 = var0.getShort();
         var4 = var0.getInt();
         boolean var16 = var0.get() == 1;
         var6 = 0;
         int var17 = 0;
         boolean var18 = false;
         if (!var16) {
            var6 = var0.getInt();
            var17 = var0.getInt();
            var18 = var0.get() == 1;
         }

         for(var9 = 0; var9 < udpEngine.connections.size(); ++var9) {
            UdpConnection var19 = (UdpConnection)udpEngine.connections.get(var9);
            if (var19 != var1) {
               sendAlarmClock_Player(var15, (long)var4, var16, var6, var17, var18, var19);
            }
         }

      } else if (var2 == AlarmClock.PacketWorld) {
         int var3 = var0.getInt();
         var4 = var0.getInt();
         int var5 = var0.getInt();
         var6 = var0.getInt();
         boolean var7 = var0.get() == 1;
         int var8 = 0;
         var9 = 0;
         boolean var10 = false;
         if (!var7) {
            var8 = var0.getInt();
            var9 = var0.getInt();
            var10 = var0.get() == 1;
         }

         IsoGridSquare var11 = ServerMap.instance.getGridSquare(var3, var4, var5);
         if (var11 == null) {
            DebugLog.log("SyncAlarmClock: sq is null x,y,z=" + var3 + "," + var4 + "," + var5);
         } else {
            AlarmClock var12 = null;

            int var13;
            for(var13 = 0; var13 < var11.getWorldObjects().size(); ++var13) {
               IsoWorldInventoryObject var14 = (IsoWorldInventoryObject)var11.getWorldObjects().get(var13);
               if (var14 != null && var14.getItem() instanceof AlarmClock && var14.getItem().id == var6) {
                  var12 = (AlarmClock)var14.getItem();
                  break;
               }
            }

            if (var12 == null) {
               DebugLog.log("SyncAlarmClock: AlarmClock is null x,y,z=" + var3 + "," + var4 + "," + var5);
            } else {
               if (var7) {
                  var12.stopRinging();
               } else {
                  var12.setHour(var8);
                  var12.setMinute(var9);
                  var12.setAlarmSet(var10);
               }

               for(var13 = 0; var13 < udpEngine.connections.size(); ++var13) {
                  UdpConnection var20 = (UdpConnection)udpEngine.connections.get(var13);
                  if (var20 != var1) {
                     sendAlarmClock_World(var3, var4, var5, (long)var6, var7, var8, var9, var10, var20);
                  }
               }
            }

         }
      }
   }

   public static void SyncIsoObject(ByteBuffer var0, UdpConnection var1) {
      if (DebugOptions.instance.Network.Server.SyncIsoObject.getValue()) {
         int var2 = var0.getInt();
         int var3 = var0.getInt();
         int var4 = var0.getInt();
         byte var5 = var0.get();
         byte var6 = var0.get();
         byte var7 = var0.get();
         if (var6 == 1) {
            IsoGridSquare var8 = ServerMap.instance.getGridSquare(var2, var3, var4);
            if (var8 != null && var5 >= 0 && var5 < var8.getObjects().size()) {
               ((IsoObject)var8.getObjects().get(var5)).syncIsoObject(true, var7, var1, var0);
            } else if (var8 != null) {
               DebugLog.log("SyncIsoObject: index=" + var5 + " is invalid x,y,z=" + var2 + "," + var3 + "," + var4);
            } else {
               DebugLog.log("SyncIsoObject: sq is null x,y,z=" + var2 + "," + var3 + "," + var4);
            }

         }
      }
   }

   public static void SyncIsoObjectReq(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      if (var2 <= 50 && var2 > 0) {
         ByteBufferWriter var3 = var1.startPacket();
         PacketTypes.doPacket((short)161, var3);
         var3.putShort(var2);

         for(int var4 = 0; var4 < var2; ++var4) {
            int var5 = var0.getInt();
            int var6 = var0.getInt();
            int var7 = var0.getInt();
            byte var8 = var0.get();
            IsoGridSquare var9 = ServerMap.instance.getGridSquare(var5, var6, var7);
            if (var9 != null && var8 >= 0 && var8 < var9.getObjects().size()) {
               ((IsoObject)var9.getObjects().get(var8)).syncIsoObjectSend(var3);
            } else if (var9 != null) {
               var3.putInt(var9.getX());
               var3.putInt(var9.getY());
               var3.putInt(var9.getZ());
               var3.putByte(var8);
               var3.putByte((byte)0);
               var3.putByte((byte)0);
            } else {
               var3.putInt(var5);
               var3.putInt(var6);
               var3.putInt(var7);
               var3.putByte(var8);
               var3.putByte((byte)2);
               var3.putByte((byte)0);
            }
         }

         var1.endPacketImmediate();
      }
   }

   public static void SyncObjectChunkHashes(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      if (var2 <= 10 && var2 > 0) {
         ByteBufferWriter var3 = var1.startPacket();
         PacketTypes.doPacket((short)164, var3);
         var3.putShort((short)2);
         int var4 = var3.bb.position();
         var3.putShort((short)0);
         int var5 = 0;

         int var6;
         for(var6 = 0; var6 < var2; ++var6) {
            int var7 = var0.getInt();
            int var8 = var0.getInt();
            long var9 = var0.getLong();
            IsoChunk var11 = ServerMap.instance.getChunk(var7, var8);
            if (var11 != null) {
               ++var5;
               var3.putShort((short)var11.wx);
               var3.putShort((short)var11.wy);
               var3.putLong(var11.getHashCodeObjects());
               int var12 = var3.bb.position();
               var3.putShort((short)0);
               int var13 = 0;

               int var14;
               for(var14 = var7 * 10; var14 < var7 * 10 + 10; ++var14) {
                  for(int var15 = var8 * 10; var15 < var8 * 10 + 10; ++var15) {
                     for(int var16 = 0; var16 <= 7; ++var16) {
                        IsoGridSquare var17 = ServerMap.instance.getGridSquare(var14, var15, var16);
                        if (var17 == null) {
                           break;
                        }

                        var3.putByte((byte)(var17.getX() - var11.wx * 10));
                        var3.putByte((byte)(var17.getY() - var11.wy * 10));
                        var3.putByte((byte)var17.getZ());
                        var3.putInt((int)var17.getHashCodeObjects());
                        ++var13;
                     }
                  }
               }

               var14 = var3.bb.position();
               var3.bb.position(var12);
               var3.putShort((short)var13);
               var3.bb.position(var14);
            }
         }

         var6 = var3.bb.position();
         var3.bb.position(var4);
         var3.putShort((short)var5);
         var3.bb.position(var6);
         var1.endPacketImmediate();
      }
   }

   public static void SyncObjectChunkHashes(IsoChunk var0, UdpConnection var1) {
      ByteBufferWriter var2 = var1.startPacket();
      PacketTypes.doPacket((short)164, var2);
      var2.putShort((short)2);
      var2.putShort((short)1);
      var2.putShort((short)var0.wx);
      var2.putShort((short)var0.wy);
      var2.putLong(var0.getHashCodeObjects());
      int var3 = var2.bb.position();
      var2.putShort((short)0);
      int var4 = 0;

      int var5;
      for(var5 = var0.wx * 10; var5 < var0.wx * 10 + 10; ++var5) {
         for(int var6 = var0.wy * 10; var6 < var0.wy * 10 + 10; ++var6) {
            for(int var7 = 0; var7 <= 7; ++var7) {
               IsoGridSquare var8 = ServerMap.instance.getGridSquare(var5, var6, var7);
               if (var8 == null) {
                  break;
               }

               var2.putByte((byte)(var8.getX() - var0.wx * 10));
               var2.putByte((byte)(var8.getY() - var0.wy * 10));
               var2.putByte((byte)var8.getZ());
               var2.putInt((int)var8.getHashCodeObjects());
               ++var4;
            }
         }
      }

      var5 = var2.bb.position();
      var2.bb.position(var3);
      var2.putShort((short)var4);
      var2.bb.position(var5);
      var1.endPacketImmediate();
   }

   public static void SyncObjectsGridSquareRequest(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      if (var2 <= 100 && var2 > 0) {
         ByteBufferWriter var3 = var1.startPacket();
         PacketTypes.doPacket((short)164, var3);
         var3.putShort((short)4);
         int var4 = var3.bb.position();
         var3.putShort((short)0);
         int var5 = 0;

         int var6;
         for(var6 = 0; var6 < var2; ++var6) {
            int var7 = var0.getInt();
            int var8 = var0.getInt();
            byte var9 = var0.get();
            IsoGridSquare var10 = ServerMap.instance.getGridSquare(var7, var8, var9);
            if (var10 != null) {
               ++var5;
               var3.putInt(var7);
               var3.putInt(var8);
               var3.putByte((byte)var9);
               var3.putByte((byte)var10.getObjects().size());
               var3.putInt(0);
               int var11 = var3.bb.position();

               int var12;
               for(var12 = 0; var12 < var10.getObjects().size(); ++var12) {
                  var3.putLong(((IsoObject)var10.getObjects().get(var12)).customHashCode());
               }

               var12 = var3.bb.position();
               var3.bb.position(var11 - 4);
               var3.putInt(var12);
               var3.bb.position(var12);
            }
         }

         var6 = var3.bb.position();
         var3.bb.position(var4);
         var3.putShort((short)var5);
         var3.bb.position(var6);
         var1.endPacketImmediate();
      }
   }

   public static void SyncObjectsRequest(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      if (var2 <= 100 && var2 > 0) {
         for(int var3 = 0; var3 < var2; ++var3) {
            int var4 = var0.getInt();
            int var5 = var0.getInt();
            byte var6 = var0.get();
            long var7 = var0.getLong();
            IsoGridSquare var9 = ServerMap.instance.getGridSquare(var4, var5, var6);
            if (var9 != null) {
               for(int var10 = 0; var10 < var9.getObjects().size(); ++var10) {
                  if (((IsoObject)var9.getObjects().get(var10)).customHashCode() == var7) {
                     ByteBufferWriter var11 = var1.startPacket();
                     PacketTypes.doPacket((short)164, var11);
                     var11.putShort((short)6);
                     var11.putInt(var4);
                     var11.putInt(var5);
                     var11.putByte((byte)var6);
                     var11.putLong(var7);
                     var11.putByte((byte)var9.getObjects().size());

                     for(int var12 = 0; var12 < var9.getObjects().size(); ++var12) {
                        var11.putLong(((IsoObject)var9.getObjects().get(var12)).customHashCode());
                     }

                     try {
                        ((IsoObject)var9.getObjects().get(var10)).writeToRemoteBuffer(var11);
                     } catch (Throwable var13) {
                        DebugLog.log("ERROR: GameServer.SyncObjectsRequest " + var13.getMessage());
                        var1.cancelPacket();
                        break;
                     }

                     var1.endPacketImmediate();
                     break;
                  }
               }
            }
         }

      }
   }

   public static void SyncDoorKey(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      byte var5 = var0.get();
      int var6 = var0.getInt();
      IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var7 != null && var5 >= 0 && var5 < var7.getObjects().size()) {
         IsoObject var8 = (IsoObject)var7.getObjects().get(var5);
         if (var8 instanceof IsoDoor) {
            IsoDoor var9 = (IsoDoor)var8;
            var9.keyId = var6;

            for(int var11 = 0; var11 < udpEngine.connections.size(); ++var11) {
               UdpConnection var12 = (UdpConnection)udpEngine.connections.get(var11);
               if (var12.getConnectedGUID() != var1.getConnectedGUID()) {
                  ByteBufferWriter var10 = var12.startPacket();
                  PacketTypes.doPacket((short)106, var10);
                  var10.putInt(var2);
                  var10.putInt(var3);
                  var10.putInt(var4);
                  var10.putByte(var5);
                  var10.putInt(var6);
                  var12.endPacketImmediate();
               }
            }

         } else {
            DebugLog.log("SyncDoorKey: expected IsoDoor index=" + var5 + " is invalid x,y,z=" + var2 + "," + var3 + "," + var4);
         }
      } else if (var7 != null) {
         DebugLog.log("SyncDoorKey: index=" + var5 + " is invalid x,y,z=" + var2 + "," + var3 + "," + var4);
      } else {
         DebugLog.log("SyncDoorKey: sq is null x,y,z=" + var2 + "," + var3 + "," + var4);
      }
   }

   public static void SyncThumpable(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      byte var5 = var0.get();
      int var6 = var0.getInt();
      byte var7 = var0.get();
      int var8 = var0.getInt();
      IsoGridSquare var9 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var9 != null && var5 >= 0 && var5 < var9.getObjects().size()) {
         IsoObject var10 = (IsoObject)var9.getObjects().get(var5);
         if (var10 instanceof IsoThumpable) {
            IsoThumpable var11 = (IsoThumpable)var10;
            var11.lockedByCode = var6;
            var11.lockedByPadlock = var7 == 1;
            var11.keyId = var8;

            for(int var13 = 0; var13 < udpEngine.connections.size(); ++var13) {
               UdpConnection var14 = (UdpConnection)udpEngine.connections.get(var13);
               if (var14.getConnectedGUID() != var1.getConnectedGUID()) {
                  ByteBufferWriter var12 = var14.startPacket();
                  PacketTypes.doPacket((short)105, var12);
                  var12.putInt(var2);
                  var12.putInt(var3);
                  var12.putInt(var4);
                  var12.putByte(var5);
                  var12.putInt(var6);
                  var12.putByte(var7);
                  var12.putInt(var8);
                  var14.endPacketImmediate();
               }
            }

         } else {
            DebugLog.log("SyncThumpable: expected IsoThumpable index=" + var5 + " is invalid x,y,z=" + var2 + "," + var3 + "," + var4);
         }
      } else if (var9 != null) {
         DebugLog.log("SyncThumpable: index=" + var5 + " is invalid x,y,z=" + var2 + "," + var3 + "," + var4);
      } else {
         DebugLog.log("SyncThumpable: sq is null x,y,z=" + var2 + "," + var3 + "," + var4);
      }
   }

   private static void RemoveItemFromMap(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var6 != null && var5 >= 0 && var5 < var6.getObjects().size()) {
         IsoObject var7 = (IsoObject)var6.getObjects().get(var5);
         if (!(var7 instanceof IsoWorldInventoryObject)) {
            IsoRegions.setPreviousFlags(var6);
         }

         DebugLog.log(DebugType.Objects, "object: removing " + var7 + " index=" + var5 + " " + var2 + "," + var3 + "," + var4);
         if (var7 instanceof IsoWorldInventoryObject) {
            LoggerManager.getLogger("item").write(var1.idStr + " \"" + var1.username + "\" floor -1 " + var2 + "," + var3 + "," + var4 + " [" + ((IsoWorldInventoryObject)var7).getItem().getFullType() + "]");
         } else {
            String var8 = var7.getName() != null ? var7.getName() : var7.getObjectName();
            if (var7.getSprite() != null && var7.getSprite().getName() != null) {
               var8 = var8 + " (" + var7.getSprite().getName() + ")";
            }

            LoggerManager.getLogger("map").write(var1.idStr + " \"" + var1.username + "\" removed " + var8 + " at " + var2 + "," + var3 + "," + var4);
         }

         int var11;
         if (var7.isTableSurface()) {
            for(var11 = var5 + 1; var11 < var6.getObjects().size(); ++var11) {
               IsoObject var9 = (IsoObject)var6.getObjects().get(var11);
               if (var9.isTableTopObject() || var9.isTableSurface()) {
                  var9.setRenderYOffset(var9.getRenderYOffset() - var7.getSurfaceOffset());
               }
            }
         }

         if (!(var7 instanceof IsoWorldInventoryObject)) {
            LuaEventManager.triggerEvent("OnObjectAboutToBeRemoved", var7);
         }

         if (!var6.getObjects().contains(var7)) {
            throw new IllegalArgumentException("OnObjectAboutToBeRemoved not allowed to remove the object");
         }

         var7.removeFromWorld();
         var7.removeFromSquare();
         var6.RecalcAllWithNeighbours(true);
         if (!(var7 instanceof IsoWorldInventoryObject)) {
            IsoWorld.instance.CurrentCell.checkHaveRoof(var2, var3);
            MapCollisionData.instance.squareChanged(var6);
            PolygonalMap2.instance.squareChanged(var6);
            ServerMap.instance.physicsCheck(var2, var3);
            IsoRegions.squareChanged(var6, true);
         }

         for(var11 = 0; var11 < udpEngine.connections.size(); ++var11) {
            UdpConnection var12 = (UdpConnection)udpEngine.connections.get(var11);
            if (var12.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var10 = var12.startPacket();
               PacketTypes.doPacket((short)23, var10);
               var10.putInt(var2);
               var10.putInt(var3);
               var10.putInt(var4);
               var10.putInt(var5);
               var12.endPacketImmediate();
            }
         }
      }

   }

   public static int RemoveItemFromMap(IsoObject var0) {
      int var1 = var0.getSquare().getX();
      int var2 = var0.getSquare().getY();
      int var3 = var0.getSquare().getZ();
      int var4 = var0.getSquare().getObjects().indexOf(var0);
      IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare(var1, var2, var3);
      if (var5 != null && !(var0 instanceof IsoWorldInventoryObject)) {
         IsoRegions.setPreviousFlags(var5);
      }

      LuaEventManager.triggerEvent("OnObjectAboutToBeRemoved", var0);
      if (!var0.getSquare().getObjects().contains(var0)) {
         throw new IllegalArgumentException("OnObjectAboutToBeRemoved not allowed to remove the object");
      } else {
         var0.removeFromWorld();
         var0.removeFromSquare();
         if (var5 != null) {
            var5.RecalcAllWithNeighbours(true);
         }

         if (!(var0 instanceof IsoWorldInventoryObject)) {
            IsoWorld.instance.CurrentCell.checkHaveRoof(var1, var2);
            MapCollisionData.instance.squareChanged(var5);
            PolygonalMap2.instance.squareChanged(var5);
            ServerMap.instance.physicsCheck(var1, var2);
            IsoRegions.squareChanged(var5, true);
         }

         for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
            UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
            if (var7.ReleventTo((float)var1, (float)var2)) {
               ByteBufferWriter var8 = var7.startPacket();
               PacketTypes.doPacket((short)23, var8);
               var8.putInt(var1);
               var8.putInt(var2);
               var8.putInt(var3);
               var8.putInt(var4);
               var7.endPacketImmediate();
            }
         }

         return var4;
      }
   }

   public static void doZombieDie(IsoZombie var0, IsoGameCharacter var1) {
      for(int var2 = 0; var2 < udpEngine.connections.size(); ++var2) {
         UdpConnection var3 = (UdpConnection)udpEngine.connections.get(var2);
         ByteBufferWriter var4 = var3.startPacket();
         PacketTypes.doPacket((short)29, var4);
         var4.putInt(var0.OnlineID);
         var4.putShort((short)((IsoPlayer)var1).OnlineID);
         var3.endPacketImmediate();
      }

   }

   public static void sendBloodSplatter(HandWeapon var0, float var1, float var2, float var3, Vector2 var4, boolean var5, boolean var6) {
      for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
         UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
         ByteBufferWriter var9 = var8.startPacket();
         PacketTypes.doPacket((short)60, var9);
         var9.putUTF(var0 != null ? var0.getType() : "");
         var9.putFloat(var1);
         var9.putFloat(var2);
         var9.putFloat(var3);
         var9.putFloat(var4.getX());
         var9.putFloat(var4.getY());
         var9.putByte((byte)(var5 ? 1 : 0));
         var9.putByte((byte)(var6 ? 1 : 0));
         byte var10 = 0;
         if (var0 != null) {
            var10 = (byte)Math.max(var0.getSplatNumber(), 1);
         }

         var9.putByte(var10);
         var8.endPacketImmediate();
      }

   }

   public static void AddItemToMap(ByteBuffer var0, UdpConnection var1) {
      IsoObject var2 = WorldItemTypes.createFromBuffer(var0);
      if (var2 instanceof IsoFire && ServerOptions.instance.NoFire.getValue()) {
         DebugLog.log("user \"" + var1.username + "\" tried to start a fire");
      } else {
         var2.loadFromRemoteBuffer(var0);
         if (var2.square != null) {
            DebugLog.log(DebugType.Objects, "object: added " + var2 + " index=" + var2.getObjectIndex() + " " + var2.getX() + "," + var2.getY() + "," + var2.getZ());
            ZLogger var10000;
            String var10001;
            if (var2 instanceof IsoWorldInventoryObject) {
               var10000 = LoggerManager.getLogger("item");
               var10001 = var1.idStr;
               var10000.write(var10001 + " \"" + var1.username + "\" floor +1 " + (int)var2.getX() + "," + (int)var2.getY() + "," + (int)var2.getZ() + " [" + ((IsoWorldInventoryObject)var2).getItem().getFullType() + "]");
            } else {
               String var3 = var2.getName() != null ? var2.getName() : var2.getObjectName();
               if (var2.getSprite() != null && var2.getSprite().getName() != null) {
                  var3 = var3 + " (" + var2.getSprite().getName() + ")";
               }

               var10000 = LoggerManager.getLogger("map");
               var10001 = var1.idStr;
               var10000.write(var10001 + " \"" + var1.username + "\" added " + var3 + " at " + var2.getX() + "," + var2.getY() + "," + var2.getZ());
            }

            var2.addToWorld();
            var2.square.RecalcProperties();
            if (!(var2 instanceof IsoWorldInventoryObject)) {
               var2.square.restackSheetRope();
               IsoWorld.instance.CurrentCell.checkHaveRoof(var2.square.getX(), var2.square.getY());
               MapCollisionData.instance.squareChanged(var2.square);
               PolygonalMap2.instance.squareChanged(var2.square);
               ServerMap.instance.physicsCheck(var2.square.x, var2.square.y);
               IsoRegions.squareChanged(var2.square);
            }

            for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
               UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var6);
               if (var4.getConnectedGUID() != var1.getConnectedGUID() && var4.ReleventTo((float)var2.square.x, (float)var2.square.y)) {
                  ByteBufferWriter var5 = var4.startPacket();
                  PacketTypes.doPacket((short)17, var5);
                  var2.writeToRemoteBuffer(var5);
                  var4.endPacketImmediate();
               }
            }

            if (!(var2 instanceof IsoWorldInventoryObject)) {
               LuaEventManager.triggerEvent("OnObjectAdded", var2);
            } else {
               ((IsoWorldInventoryObject)var2).dropTime = GameTime.getInstance().getWorldAgeHours();
            }
         } else if (bDebug) {
            DebugLog.log("AddItemToMap: sq is null");
         }

      }
   }

   public static void sendDeleteZombie(IsoZombie var0) {
      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);
         if (var2.ReleventTo(var0.x, var0.y)) {
            ByteBufferWriter var3 = var2.startPacket();
            PacketTypes.doPacket((short)30, var3);
            var3.putShort(var0.OnlineID);
            var2.endPacketImmediate();
         }
      }

   }

   public static void disconnect(UdpConnection var0) {
      if (var0.playerDownloadServer != null) {
         try {
            var0.playerDownloadServer.destroy();
         } catch (Exception var3) {
            var3.printStackTrace();
         }

         var0.playerDownloadServer = null;
      }

      int var1;
      for(var1 = 0; var1 < 4; ++var1) {
         IsoPlayer var2 = var0.players[var1];
         if (var2 != null) {
            ChatServer.getInstance().disconnectPlayer(var0.playerIDs[var1]);
            disconnectPlayer(var2, var0);
         }

         var0.usernames[var1] = null;
         var0.players[var1] = null;
         var0.playerIDs[var1] = -1;
         var0.ReleventPos[var1] = null;
         var0.connectArea[var1] = null;
      }

      for(var1 = 0; var1 < udpEngine.getMaxConnections(); ++var1) {
         if (SlotToConnection[var1] == var0) {
            SlotToConnection[var1] = null;
         }
      }

      Iterator var4 = IDToAddressMap.entrySet().iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         if ((Long)var5.getValue() == var0.getConnectedGUID()) {
            var4.remove();
         }
      }

      if (!SteamUtils.isSteamModeEnabled()) {
         PublicServerUtil.updatePlayers();
      }

      if (CoopSlave.instance != null && var0.isCoopHost) {
         DebugLog.log("Host user disconnected, stopping the server");
         ServerMap.instance.QueueQuit();
      }

   }

   public static void addIncoming(short var0, ByteBuffer var1, UdpConnection var2) {
      ZomboidNetData var3 = null;
      if (var1.limit() > 2048) {
         var3 = ZomboidNetDataPool.instance.getLong(var1.limit());
      } else {
         var3 = ZomboidNetDataPool.instance.get();
      }

      var3.read(var0, var1, var2);
      var3.time = System.currentTimeMillis();
      if (var3.type == 218) {
         short var4 = var3.buffer.getShort();
         var3.buffer.position(0);
         synchronized(MainLoopPlayerUpdate) {
            if (MainLoopPlayerUpdate.containsKey(Integer.valueOf(var4))) {
               MainLoopPlayerUpdate.replace(Integer.valueOf(var4), var3);
            } else {
               MainLoopPlayerUpdate.put(Integer.valueOf(var4), var3);
            }
         }
      } else if (var3.type == 5) {
         byte var14 = var3.buffer.get();
         var3.buffer.position(0);
         if (var14 == 9) {
            synchronized(MainLoopNetData) {
               MainLoopNetData.add(var3);
            }
         } else {
            synchronized(MainLoopNetDataHighPrioritet) {
               MainLoopNetDataHighPrioritet.add(var3);
            }
         }
      } else {
         synchronized(MainLoopNetDataHighPrioritet) {
            MainLoopNetDataHighPrioritet.add(var3);
         }
      }

   }

   public static void smashWindow(IsoWindow var0, int var1) {
      for(int var2 = 0; var2 < udpEngine.connections.size(); ++var2) {
         UdpConnection var3 = (UdpConnection)udpEngine.connections.get(var2);
         if (var3.ReleventTo(var0.getX(), var0.getY())) {
            ByteBufferWriter var4 = var3.startPacket();
            PacketTypes.doPacket((short)32, var4);
            var4.putInt(var0.square.getX());
            var4.putInt(var0.square.getY());
            var4.putInt(var0.square.getZ());
            var4.putByte((byte)var0.square.getObjects().indexOf(var0));
            var4.putByte((byte)var1);
            var3.endPacketImmediate();
         }
      }

   }

   public static void SendDeath(UdpConnection var0, IsoPlayer var1) {
      var1.getBodyDamage().setOverallBodyHealth(-1.0F);

      for(int var2 = 0; var2 < udpEngine.connections.size(); ++var2) {
         UdpConnection var3 = (UdpConnection)udpEngine.connections.get(var2);
         if (var3 != var0) {
            ByteBufferWriter var4 = var3.startPacket();
            PacketTypes.doPacket((short)33, var4);
            var4.putInt(var1.OnlineID);
            var4.putFloat(var1.getX());
            var4.putFloat(var1.getY());
            var4.putFloat(var1.getZ());
            var4.putInt(var1.getDir().index());

            try {
               ArrayList var5 = var1.getInventory().save(var4.bb);
               var4.putByte((byte)var1.getWornItems().size());
               var1.getWornItems().forEach((var2x) -> {
                  GameWindow.WriteString(var3.bb, var2x.getLocation());
                  var3.putShort((short)var4.indexOf(var2x.getItem()));
               });
            } catch (IOException var6) {
               var6.printStackTrace();
            }

            var3.endPacketImmediate();
         }
      }

   }

   public static void SendDeath(IsoPlayer var0) {
      var0.getBodyDamage().setOverallBodyHealth(-1.0F);

      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);
         ByteBufferWriter var3 = var2.startPacket();
         PacketTypes.doPacket((short)33, var3);
         var3.putInt(var0.OnlineID);
         var3.putFloat(var0.getX());
         var3.putFloat(var0.getY());
         var3.putFloat(var0.getZ());
         var3.putInt(var0.getDir().index());

         try {
            ArrayList var4 = var0.getInventory().save(var3.bb);
            var3.putByte((byte)var0.getWornItems().size());
            var0.getWornItems().forEach((var2x) -> {
               GameWindow.WriteString(var3.bb, var2x.getLocation());
               var3.putShort((short)var4.indexOf(var2x.getItem()));
            });
         } catch (IOException var5) {
            var5.printStackTrace();
         }

         var2.endPacketImmediate();
      }

   }

   public static void SendOnBeaten(IsoPlayer var0, float var1, float var2, float var3) {
      var0.getBodyDamage().setOverallBodyHealth(-1.0F);

      for(int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
         UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var4);
         ByteBufferWriter var6 = var5.startPacket();
         PacketTypes.doPacket((short)165, var6);
         var6.putInt(var0.OnlineID);
         var6.putFloat(var1);
         var6.putFloat(var2);
         var6.putFloat(var3);
         var5.endPacketImmediate();
      }

   }

   public static boolean doSendZombies() {
      return SendZombies == 0;
   }

   private static void receiveDeadZombie(ByteBuffer var0, UdpConnection var1) {
      try {
         DeadBodyPacket var2 = new DeadBodyPacket();
         var2.parse(var0);
         DebugLog.log(DebugType.Multiplayer, "DeadBody receive: " + var2.getDescription());
         if (var2.zombie != null && var2.zombie.networkAI.deadZombie == null) {
            var2.zombie.networkAI.deadZombie = var2;
         }
      } catch (Exception var3) {
         DebugLog.log("DeadZombie receive: failed");
         var3.printStackTrace();
      }

   }

   public static void sendDeadZombie(IsoZombie var0) {
      if (var0.networkAI.deadZombie == null) {
         var0.networkAI.deadZombie = new DeadBodyPacket();
         var0.networkAI.deadZombie.set(var0);
      }

      var0.networkAI.deadZombie.isServer = true;
      DebugLog.log(DebugType.Multiplayer, "DeadBody send: " + var0.networkAI.deadZombie.getDescription());

      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);
         if (var2.ReleventTo(var0.x, var0.y)) {
            ByteBufferWriter var3 = var2.startPacket();
            PacketTypes.doPacket((short)39, var3);
            var0.networkAI.deadZombie.write(var3);
            if (var0.getInventory() != null) {
               var3.putByte((byte)1);

               try {
                  int var4 = -1;
                  Iterator var5 = var0.getInventory().getItems().iterator();

                  while(var5.hasNext()) {
                     InventoryItem var6 = (InventoryItem)var5.next();
                     if (PersistentOutfits.instance.isHatFallen(var0.getPersistentOutfitID()) && var6.getScriptItem() != null && var6.getScriptItem().getChanceToFall() > 0) {
                        var4 = var6.id;
                     }
                  }

                  if (var4 != -1) {
                     var0.getInventory().removeItemWithID(var4);
                  }

                  ArrayList var11 = var0.getInventory().save(var3.bb);
                  WornItems var12 = var0.getWornItems();
                  if (var12 == null) {
                     var3.bb.put((byte)0);
                  } else {
                     if (var12.size() > 127) {
                        throw new RuntimeException("too many worn items");
                     }

                     var3.bb.put((byte)var12.size());

                     for(int var7 = 0; var7 < var12.size(); ++var7) {
                        WornItem var8 = var12.get(var7);
                        if (PersistentOutfits.instance.isHatFallen(var0.getPersistentOutfitID()) && var8.getItem().getScriptItem() != null && var8.getItem().getScriptItem().getChanceToFall() > 0) {
                           var3.putUTF("");
                           var3.bb.putShort((short)-1);
                        } else {
                           var3.putUTF(var8.getLocation());
                           var3.bb.putShort((short)var11.indexOf(var8.getItem()));
                        }
                     }
                  }

                  AttachedItems var13 = var0.getAttachedItems();
                  if (var13 == null) {
                     var3.putByte((byte)0);
                  } else {
                     if (var13.size() > 127) {
                        throw new RuntimeException("too many attached items");
                     }

                     var3.putByte((byte)var13.size());

                     for(int var14 = 0; var14 < var13.size(); ++var14) {
                        AttachedItem var9 = var13.get(var14);
                        var3.putUTF(var9.getLocation());
                        var3.putShort((short)var11.indexOf(var9.getItem()));
                     }
                  }
               } catch (IOException var10) {
                  var10.printStackTrace();
               }
            } else {
               var3.putByte((byte)0);
            }

            var2.endPacketImmediate();
         }
      }

   }

   public static void doDamage(IsoGameCharacter var0, float var1) {
      if (var0 != null) {
         if (PlayerToAddressMap.containsKey((IsoPlayer)var0)) {
            long var2 = (Long)PlayerToAddressMap.get((IsoPlayer)var0);
            UdpConnection var4 = udpEngine.getActiveConnection(var2);
            if (var4 != null) {
               ByteBufferWriter var5 = var4.startPacket();
               IsoPlayer var6 = (IsoPlayer)var0;
               PacketTypes.doPacket((short)41, var5);
               var5.putShort((short)var6.OnlineID);

               try {
                  var6.getBodyDamage().save(var5.bb);
               } catch (IOException var8) {
                  var8.printStackTrace();
               }

               var5.putFloat(var1);
               var4.endPacketImmediate();
            }
         }
      }
   }

   private static void sendStartRain(UdpConnection var0) {
      ByteBufferWriter var1 = var0.startPacket();
      PacketTypes.doPacket((short)77, var1);
      var1.putInt(RainManager.randRainMin);
      var1.putInt(RainManager.randRainMax);
      var1.putFloat(RainManager.RainDesiredIntensity);
      var0.endPacketImmediate();
   }

   public static void startRain() {
      if (udpEngine != null) {
         for(int var0 = 0; var0 < udpEngine.connections.size(); ++var0) {
            UdpConnection var1 = (UdpConnection)udpEngine.connections.get(var0);
            sendStartRain(var1);
         }

      }
   }

   private static void sendStopRain(UdpConnection var0) {
      ByteBufferWriter var1 = var0.startPacket();
      PacketTypes.doPacket((short)78, var1);
      var0.endPacketImmediate();
   }

   public static void stopRain() {
      for(int var0 = 0; var0 < udpEngine.connections.size(); ++var0) {
         UdpConnection var1 = (UdpConnection)udpEngine.connections.get(var0);
         sendStopRain(var1);
      }

   }

   private static void sendWeather(UdpConnection var0) {
      GameTime var1 = GameTime.getInstance();
      ByteBufferWriter var2 = var0.startPacket();
      PacketTypes.doPacket((short)64, var2);
      var2.putByte((byte)var1.getDawn());
      var2.putByte((byte)var1.getDusk());
      var2.putByte((byte)(var1.isThunderDay() ? 1 : 0));
      var2.putFloat(var1.Moon);
      var2.putFloat(var1.getAmbientMin());
      var2.putFloat(var1.getAmbientMax());
      var2.putFloat(var1.getViewDistMin());
      var2.putFloat(var1.getViewDistMax());
      var2.putFloat(IsoWorld.instance.getGlobalTemperature());
      var2.putUTF(IsoWorld.instance.getWeather());
      ErosionMain.getInstance().sendState(var2.bb);
      var0.endPacketImmediate();
   }

   public static void sendWeather() {
      for(int var0 = 0; var0 < udpEngine.connections.size(); ++var0) {
         UdpConnection var1 = (UdpConnection)udpEngine.connections.get(var0);
         sendWeather(var1);
      }

   }

   private static void syncClock(UdpConnection var0) {
      GameTime var1 = GameTime.getInstance();
      ByteBufferWriter var2 = var0.startPacket();
      PacketTypes.doPacket((short)19, var2);
      var2.putBoolean(bFastForward);
      var2.putFloat(var1.getTimeOfDay());
      var0.endPacketImmediate();
   }

   public static void syncClock() {
      for(int var0 = 0; var0 < udpEngine.connections.size(); ++var0) {
         UdpConnection var1 = (UdpConnection)udpEngine.connections.get(var0);
         syncClock(var1);
      }

   }

   public static void sendServerCommand(String var0, String var1, KahluaTable var2, UdpConnection var3) {
      ByteBufferWriter var4 = var3.startPacket();
      PacketTypes.doPacket((short)57, var4);
      var4.putUTF(var0);
      var4.putUTF(var1);
      if (var2 != null && !var2.isEmpty()) {
         var4.putByte((byte)1);

         try {
            KahluaTableIterator var5 = var2.iterator();

            while(var5.advance()) {
               if (!TableNetworkUtils.canSave(var5.getKey(), var5.getValue())) {
                  Object var10000 = var5.getKey();
                  DebugLog.log("ERROR: sendServerCommand: can't save key,value=" + var10000 + "," + var5.getValue());
               }
            }

            TableNetworkUtils.save(var2, var4.bb);
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      } else {
         var4.putByte((byte)0);
      }

      var3.endPacketImmediate();
   }

   public static void sendServerCommand(String var0, String var1, KahluaTable var2) {
      for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
         sendServerCommand(var0, var1, var2, var4);
      }

   }

   public static void sendServerCommandV(String var0, String var1, Object... var2) {
      if (var2.length == 0) {
         sendServerCommand(var0, var1, (KahluaTable)null);
      } else if (var2.length % 2 != 0) {
         DebugLog.log("ERROR: sendServerCommand called with invalid number of arguments (" + var0 + " " + var1 + ")");
      } else {
         KahluaTable var3 = LuaManager.platform.newTable();

         for(int var4 = 0; var4 < var2.length; var4 += 2) {
            Object var5 = var2[var4 + 1];
            if (var5 instanceof Float) {
               var3.rawset(var2[var4], ((Float)var5).doubleValue());
            } else if (var5 instanceof Integer) {
               var3.rawset(var2[var4], ((Integer)var5).doubleValue());
            } else if (var5 instanceof Short) {
               var3.rawset(var2[var4], ((Short)var5).doubleValue());
            } else {
               var3.rawset(var2[var4], var5);
            }
         }

         sendServerCommand(var0, var1, var3);
      }
   }

   public static void sendServerCommand(IsoPlayer var0, String var1, String var2, KahluaTable var3) {
      if (PlayerToAddressMap.containsKey(var0)) {
         long var4 = (Long)PlayerToAddressMap.get(var0);
         UdpConnection var6 = udpEngine.getActiveConnection(var4);
         if (var6 != null) {
            sendServerCommand(var1, var2, var3, var6);
         }
      }
   }

   public static ArrayList<IsoPlayer> getPlayers() {
      ArrayList var0 = new ArrayList();

      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);

         for(int var3 = 0; var3 < 4; ++var3) {
            IsoPlayer var4 = var2.players[var3];
            if (var4 != null && var4.OnlineID != -1) {
               var0.add(var4);
            }
         }
      }

      return var0;
   }

   public static int getPlayerCount() {
      int var0 = 0;

      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);

         for(int var3 = 0; var3 < 4; ++var3) {
            if (var2.playerIDs[var3] != -1) {
               ++var0;
            }
         }
      }

      return var0;
   }

   public static void sendAmbient(String var0, int var1, int var2, int var3, float var4) {
      DebugLog.log(DebugType.Sound, "ambient: sending " + var0 + " at " + var1 + "," + var2 + " radius=" + var3);

      for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
         UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
         IsoPlayer var7 = getAnyPlayerFromConnection(var6);
         if (var7 != null) {
            ByteBufferWriter var8 = var6.startPacket();
            PacketTypes.doPacket((short)55, var8);
            var8.putUTF(var0);
            var8.putInt(var1);
            var8.putInt(var2);
            var8.putInt(var3);
            var8.putFloat(var4);
            var6.endPacketImmediate();
         }
      }

   }

   public static void toggleSafety(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      IsoPlayer var3 = getPlayerFromConnection(var1, var2);
      if (var3 != null) {
         var3.setSafety(!var3.isSafety());
         ZLogger var10000;
         String var10001;
         if (var3.isSafety()) {
            var10000 = LoggerManager.getLogger("pvp");
            var10001 = var3.username;
            var10000.write("user " + var10001 + " " + LoggerManager.getPlayerCoords(var3) + " enabled safety");
         } else {
            var10000 = LoggerManager.getLogger("pvp");
            var10001 = var3.username;
            var10000.write("user " + var10001 + " " + LoggerManager.getPlayerCoords(var3) + " disabled safety");
         }

         for(int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
            UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var4);
            if (var5.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var6 = var5.startPacket();
               PacketTypes.doPacket((short)86, var6);
               var6.putInt(var3.OnlineID);
               var6.putByte((byte)(var3.isSafety() ? 1 : 0));
               var5.endPacketImmediate();
            }
         }

      }
   }

   public static void updateOverlayForClients(IsoObject var0, String var1, float var2, float var3, float var4, float var5, UdpConnection var6) {
      if (udpEngine != null) {
         for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
            if (var8 != null && var0.square != null && var8.ReleventTo((float)var0.square.x, (float)var0.square.y) && (var6 == null || var8.getConnectedGUID() != var6.getConnectedGUID())) {
               ByteBufferWriter var9 = var8.startPacket();
               PacketTypes.doPacket((short)90, var9);
               GameWindow.WriteStringUTF(var9.bb, var1);
               var9.putInt(var0.getSquare().getX());
               var9.putInt(var0.getSquare().getY());
               var9.putInt(var0.getSquare().getZ());
               var9.putFloat(var2);
               var9.putFloat(var3);
               var9.putFloat(var4);
               var9.putFloat(var5);
               var9.putInt(var0.getSquare().getObjects().indexOf(var0));
               var8.endPacketImmediate();
            }
         }

      }
   }

   private static void updateOverlayFromClient(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadStringUTF(var0);
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      float var6 = var0.getFloat();
      float var7 = var0.getFloat();
      float var8 = var0.getFloat();
      float var9 = var0.getFloat();
      int var10 = var0.getInt();
      IsoGridSquare var11 = IsoWorld.instance.CurrentCell.getGridSquare(var3, var4, var5);
      if (var11 != null && var10 < var11.getObjects().size()) {
         try {
            IsoObject var12 = (IsoObject)var11.getObjects().get(var10);
            if (var12 != null && var12.setOverlaySprite(var2, var6, var7, var8, var9, false)) {
               updateOverlayForClients(var12, var2, var6, var7, var8, var9, var1);
            }
         } catch (Exception var13) {
         }
      }

   }

   public static void sendReanimatedZombieID(IsoPlayer var0, IsoZombie var1) {
      if (PlayerToAddressMap.containsKey(var0)) {
         sendObjectChange(var0, "reanimatedID", (Object[])("ID", (double)var1.OnlineID));
      }

   }

   private static void syncSafehouse(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      String var6 = GameWindow.ReadString(var0);
      int var7 = var0.getInt();
      SafeHouse var8 = SafeHouse.getSafeHouse(var2, var3, var4, var5);
      boolean var9 = false;
      if (var8 == null) {
         var8 = SafeHouse.addSafeHouse(var2, var3, var4, var5, var6, false);
         var9 = true;
      }

      if (var8 != null) {
         var8.getPlayers().clear();

         int var10;
         for(var10 = 0; var10 < var7; ++var10) {
            String var11 = GameWindow.ReadString(var0);
            var8.addPlayer(var11);
         }

         var10 = var0.getInt();
         var8.playersRespawn.clear();

         for(int var13 = 0; var13 < var10; ++var13) {
            String var12 = GameWindow.ReadString(var0);
            var8.playersRespawn.add(var12);
         }

         boolean var14 = var0.get() == 1;
         var8.setTitle(GameWindow.ReadString(var0));
         var8.setOwner(var6);
         sendSafehouse(var8, var14, var1);
         if (ChatServer.isInited()) {
            if (var9) {
               ChatServer.getInstance().createSafehouseChat(var8.getId());
            }

            if (var14) {
               ChatServer.getInstance().removeSafehouseChat(var8.getId());
            } else {
               ChatServer.getInstance().syncSafehouseChatMembers(var8.getId(), var6, var8.getPlayers());
            }
         }

         if (var14) {
            SafeHouse.getSafehouseList().remove(var8);
            DebugLog.log("safehouse: removed " + var2 + "," + var3 + "," + var4 + "," + var5 + " owner=" + var8.getOwner());
         }

      }
   }

   public static void sendSafehouse(SafeHouse var0, boolean var1, UdpConnection var2) {
      for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
         if (var2 == null || var4.getConnectedGUID() != var2.getConnectedGUID()) {
            ByteBufferWriter var5 = var4.startPacket();
            PacketTypes.doPacket((short)114, var5);
            var5.putInt(var0.getX());
            var5.putInt(var0.getY());
            var5.putInt(var0.getW());
            var5.putInt(var0.getH());
            var5.putUTF(var0.getOwner());
            var5.putInt(var0.getPlayers().size());
            Iterator var6 = var0.getPlayers().iterator();

            while(var6.hasNext()) {
               String var7 = (String)var6.next();
               var5.putUTF(var7);
            }

            var5.putInt(var0.playersRespawn.size());

            for(int var8 = 0; var8 < var0.playersRespawn.size(); ++var8) {
               var5.putUTF((String)var0.playersRespawn.get(var8));
            }

            var5.putBoolean(var1);
            var5.putUTF(var0.getTitle());
            var4.endPacketImmediate();
         }
      }

   }

   private static void dealWithNetDataShort(ZomboidNetData var0, ByteBuffer var1, UdpConnection var2) {
      short var3 = var1.getShort();
      switch(var3) {
      case 1000:
         receiveWaveSignal(var1);
         break;
      case 1001:
         receivePlayerListensChannel(var1);
         break;
      case 1002:
         sendRadioServerData(var2);
         break;
      case 1004:
         receiveRadioDeviceDataState(var1, var2);
         break;
      case 1200:
         SyncCustomLightSwitchSettings(var1, var2);
      }

   }

   public static void receiveRadioDeviceDataState(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      if (var2 == 1) {
         int var3 = var0.getInt();
         int var4 = var0.getInt();
         int var5 = var0.getInt();
         int var6 = var0.getInt();
         IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare(var3, var4, var5);
         if (var7 != null && var6 >= 0 && var6 < var7.getObjects().size()) {
            IsoObject var8 = (IsoObject)var7.getObjects().get(var6);
            if (var8 instanceof IsoWaveSignal) {
               DeviceData var9 = ((IsoWaveSignal)var8).getDeviceData();
               if (var9 != null) {
                  try {
                     var9.receiveDeviceDataStatePacket(var0, (UdpConnection)null);
                  } catch (Exception var13) {
                     System.out.print(var13.getMessage());
                  }
               }
            }
         }
      } else if (var2 == 0) {
         byte var14 = var0.get();
         IsoPlayer var16 = getPlayerFromConnection(var1, var14);
         byte var18 = var0.get();
         if (var16 != null) {
            Radio var20 = null;
            if (var18 == 1 && var16.getPrimaryHandItem() instanceof Radio) {
               var20 = (Radio)var16.getPrimaryHandItem();
            }

            if (var18 == 2 && var16.getSecondaryHandItem() instanceof Radio) {
               var20 = (Radio)var16.getSecondaryHandItem();
            }

            if (var20 != null && var20.getDeviceData() != null) {
               try {
                  var20.getDeviceData().receiveDeviceDataStatePacket(var0, var1);
               } catch (Exception var12) {
                  System.out.print(var12.getMessage());
               }
            }
         }
      } else if (var2 == 2) {
         short var15 = var0.getShort();
         short var17 = var0.getShort();
         BaseVehicle var19 = VehicleManager.instance.getVehicleByID(var15);
         if (var19 != null) {
            VehiclePart var22 = var19.getPartByIndex(var17);
            if (var22 != null) {
               DeviceData var21 = var22.getDeviceData();
               if (var21 != null) {
                  try {
                     var21.receiveDeviceDataStatePacket(var0, (UdpConnection)null);
                  } catch (Exception var11) {
                     System.out.print(var11.getMessage());
                  }
               }
            }
         }
      }

   }

   private static void sendRadioServerData(UdpConnection var0) {
      ByteBufferWriter var1 = var0.startPacket();
      PacketTypesShort.doPacket((short)1002, var1);
      ZomboidRadio.getInstance().WriteRadioServerDataPacket(var1);
      var0.endPacketImmediate();
   }

   public static void sendIsoWaveSignal(int var0, int var1, int var2, String var3, String var4, float var5, float var6, float var7, int var8, boolean var9) {
      for(int var10 = 0; var10 < udpEngine.connections.size(); ++var10) {
         UdpConnection var11 = (UdpConnection)udpEngine.connections.get(var10);
         ByteBufferWriter var12 = var11.startPacket();
         PacketTypesShort.doPacket((short)1000, var12);
         var12.putInt(var0);
         var12.putInt(var1);
         var12.putInt(var2);
         var12.putBoolean(var3 != null);
         if (var3 != null) {
            GameWindow.WriteString(var12.bb, var3);
         }

         var12.putByte((byte)(var4 != null ? 1 : 0));
         if (var4 != null) {
            var12.putUTF(var4);
         }

         var12.putFloat(var5);
         var12.putFloat(var6);
         var12.putFloat(var7);
         var12.putInt(var8);
         var12.putByte((byte)(var9 ? 1 : 0));
         var11.endPacketImmediate();
      }

   }

   public static void receiveWaveSignal(ByteBuffer var0) {
      int var1 = var0.getInt();
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      boolean var4 = var0.get() == 1;
      String var5 = null;
      if (var4) {
         var5 = GameWindow.ReadString(var0);
      }

      String var6 = null;
      if (var0.get() == 1) {
         var6 = GameWindow.ReadString(var0);
      }

      float var7 = var0.getFloat();
      float var8 = var0.getFloat();
      float var9 = var0.getFloat();
      int var10 = var0.getInt();
      boolean var11 = var0.get() == 1;
      ZomboidRadio.getInstance().ReceiveTransmission(var1, var2, var3, var5, var6, var7, var8, var9, var10, var11);
   }

   public static void receivePlayerListensChannel(ByteBuffer var0) {
      int var1 = var0.getInt();
      boolean var2 = var0.get() == 1;
      boolean var3 = var0.get() == 1;
      ZomboidRadio.getInstance().PlayerListensChannel(var1, var2, var3);
   }

   public static void sendAlarm(int var0, int var1) {
      for(int var2 = 0; var2 < udpEngine.connections.size(); ++var2) {
         UdpConnection var3 = (UdpConnection)udpEngine.connections.get(var2);
         IsoPlayer var4 = getAnyPlayerFromConnection(var3);
         if (var4 != null) {
            ByteBufferWriter var5 = var3.startPacket();
            PacketTypes.doPacket((short)118, var5);
            var5.putInt(var0);
            var5.putInt(var1);
            var3.endPacketImmediate();
         }
      }

   }

   public static boolean isSpawnBuilding(BuildingDef var0) {
      return SpawnPoints.instance.isSpawnBuilding(var0);
   }

   private static void setFastForward(boolean var0) {
      if (var0 != bFastForward) {
         bFastForward = var0;
         syncClock();
         if (!bFastForward) {
            SendZombies = 0;
         }

      }
   }

   private static void receiveCustomColor(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      float var6 = var0.getFloat();
      float var7 = var0.getFloat();
      float var8 = var0.getFloat();
      float var9 = var0.getFloat();
      IsoGridSquare var10 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var10 != null && var5 < var10.getObjects().size()) {
         IsoObject var11 = (IsoObject)var10.getObjects().get(var5);
         if (var11 != null) {
            var11.setCustomColor(var6, var7, var8, var9);
         }
      }

      for(int var14 = 0; var14 < udpEngine.connections.size(); ++var14) {
         UdpConnection var12 = (UdpConnection)udpEngine.connections.get(var14);
         if (var12.ReleventTo((float)var2, (float)var3) && (var1 != null && var12.getConnectedGUID() != var1.getConnectedGUID() || var1 == null)) {
            ByteBufferWriter var13 = var12.startPacket();
            PacketTypes.doPacket((short)121, var13);
            var13.putInt(var2);
            var13.putInt(var3);
            var13.putInt(var4);
            var13.putInt(var5);
            var13.putFloat(var6);
            var13.putFloat(var7);
            var13.putFloat(var8);
            var13.putFloat(var9);
            var12.endPacketImmediate();
         }
      }

   }

   private static void receiveFurnaceChange(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var5 == null) {
         DebugLog.log("receiveFurnaceChange: square is null x,y,z=" + var2 + "," + var3 + "," + var4);
      } else {
         BSFurnace var6 = null;

         for(int var7 = 0; var7 < var5.getObjects().size(); ++var7) {
            if (var5.getObjects().get(var7) instanceof BSFurnace) {
               var6 = (BSFurnace)var5.getObjects().get(var7);
               break;
            }
         }

         if (var6 == null) {
            DebugLog.log("receiveFurnaceChange: furnace is null x,y,z=" + var2 + "," + var3 + "," + var4);
         } else {
            var6.fireStarted = var0.get() == 1;
            var6.fuelAmount = var0.getFloat();
            var6.fuelDecrease = var0.getFloat();
            var6.heat = var0.getFloat();
            var6.sSprite = GameWindow.ReadString(var0);
            var6.sLitSprite = GameWindow.ReadString(var0);
            sendFuranceChange(var6, var1);
         }
      }
   }

   public static void sendFuranceChange(BSFurnace var0, UdpConnection var1) {
      for(int var2 = 0; var2 < udpEngine.connections.size(); ++var2) {
         UdpConnection var3 = (UdpConnection)udpEngine.connections.get(var2);
         if (var3.ReleventTo((float)var0.square.x, (float)var0.square.y) && (var1 != null && var3.getConnectedGUID() != var1.getConnectedGUID() || var1 == null)) {
            ByteBufferWriter var4 = var3.startPacket();
            PacketTypes.doPacket((short)120, var4);
            var4.putInt(var0.square.x);
            var4.putInt(var0.square.y);
            var4.putInt(var0.square.z);
            var4.putByte((byte)(var0.isFireStarted() ? 1 : 0));
            var4.putFloat(var0.getFuelAmount());
            var4.putFloat(var0.getFuelDecrease());
            var4.putFloat(var0.getHeat());
            GameWindow.WriteString(var4.bb, var0.sSprite);
            GameWindow.WriteString(var4.bb, var0.sLitSprite);
            var3.endPacketImmediate();
         }
      }

   }

   private static void sendUserlog(ByteBuffer var0, UdpConnection var1, String var2) {
      ArrayList var3 = ServerWorldDatabase.instance.getUserlog(var2);

      for(int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
         UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var4);
         if (var5.getConnectedGUID() == var1.getConnectedGUID()) {
            ByteBufferWriter var6 = var5.startPacket();
            PacketTypes.doPacket((short)128, var6);
            var6.putInt(var3.size());
            var6.putUTF(var2);

            for(int var7 = 0; var7 < var3.size(); ++var7) {
               Userlog var8 = (Userlog)var3.get(var7);
               var6.putInt(UserlogType.FromString(var8.getType()).index());
               var6.putUTF(var8.getText());
               var6.putUTF(var8.getIssuedBy());
               var6.putInt(var8.getAmount());
            }

            var5.endPacketImmediate();
         }
      }

   }

   private static void addUserlog(ByteBuffer var0, UdpConnection var1) throws SQLException {
      String var2 = GameWindow.ReadString(var0);
      String var3 = GameWindow.ReadString(var0);
      String var4 = GameWindow.ReadString(var0);
      ServerWorldDatabase.instance.addUserlog(var2, UserlogType.FromString(var3), var4, var1.username, 1);
      LoggerManager.getLogger("admin").write(var1.username + " added log on user " + var2 + ", log: " + var4);
   }

   private static void removeUserlog(ByteBuffer var0, UdpConnection var1) throws SQLException {
      String var2 = GameWindow.ReadString(var0);
      String var3 = GameWindow.ReadString(var0);
      String var4 = GameWindow.ReadString(var0);
      ServerWorldDatabase.instance.removeUserLog(var2, var3, var4);
      LoggerManager.getLogger("admin").write(var1.username + " removed log on user " + var2 + ", type:" + var3 + ", log: " + var4);
   }

   private static void addWarningPoint(ByteBuffer var0, UdpConnection var1) throws SQLException {
      String var2 = GameWindow.ReadString(var0);
      String var3 = GameWindow.ReadString(var0);
      int var4 = var0.getInt();
      ServerWorldDatabase.instance.addWarningPoint(var2, var3, var4, var1.username);
      LoggerManager.getLogger("admin").write(var1.username + " added " + var4 + " warning point(s) on " + var2 + ", reason:" + var3);

      for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
         UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
         if (var6.username.equals(var2)) {
            ByteBufferWriter var7 = var6.startPacket();
            PacketTypes.doPacket((short)79, var7);
            var7.putUTF(var1.username);
            var7.putUTF(" gave you " + var4 + " warning point(s), reason: " + var3 + " ");
            var6.endPacketImmediate();
         }
      }

   }

   public static void sendAdminMessage(String var0, int var1, int var2, int var3) {
      for(int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
         UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var4);
         if (canSeePlayerStats(var5)) {
            ByteBufferWriter var6 = var5.startPacket();
            PacketTypes.doPacket((short)132, var6);
            var6.putUTF(var0);
            var6.putInt(var1);
            var6.putInt(var2);
            var6.putInt(var3);
            var5.endPacketImmediate();
         }
      }

   }

   private static void wakeUpPlayer(ByteBuffer var0, UdpConnection var1) {
      IsoPlayer var2 = (IsoPlayer)IDToPlayerMap.get(var0.getInt());
      var2.setAsleep(false);
      var2.setAsleepTime(0.0F);

      for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
         if (var4.getConnectedGUID() != var1.getConnectedGUID()) {
            ByteBufferWriter var5 = var4.startPacket();
            PacketTypes.doPacket((short)133, var5);
            var5.putInt(var2.OnlineID);
            var4.endPacketImmediate();
         }
      }

   }

   private static void sendDBSchema(ByteBuffer var0, UdpConnection var1) {
      DBSchema var2 = ServerWorldDatabase.instance.getDBSchema();

      for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
         if (var1 != null && var4.getConnectedGUID() == var1.getConnectedGUID()) {
            ByteBufferWriter var5 = var4.startPacket();
            PacketTypes.doPacket((short)135, var5);
            HashMap var6 = var2.getSchema();
            var5.putInt(var6.size());
            Iterator var7 = var6.keySet().iterator();

            while(var7.hasNext()) {
               String var8 = (String)var7.next();
               HashMap var9 = (HashMap)var6.get(var8);
               var5.putUTF(var8);
               var5.putInt(var9.size());
               Iterator var10 = var9.keySet().iterator();

               while(var10.hasNext()) {
                  String var11 = (String)var10.next();
                  var5.putUTF(var11);
                  var5.putUTF((String)var9.get(var11));
               }
            }

            var4.endPacketImmediate();
         }
      }

   }

   private static void sendTableResult(ByteBuffer var0, UdpConnection var1) throws SQLException {
      int var2 = var0.getInt();
      String var3 = GameWindow.ReadString(var0);
      ArrayList var4 = ServerWorldDatabase.instance.getTableResult(var3);

      for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
         UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
         if (var1 != null && var6.getConnectedGUID() == var1.getConnectedGUID()) {
            doTableResult(var6, var3, var4, 0, var2);
         }
      }

   }

   private static void doTableResult(UdpConnection var0, String var1, ArrayList<DBResult> var2, int var3, int var4) {
      int var5 = 0;
      boolean var6 = true;
      ByteBufferWriter var7 = var0.startPacket();
      PacketTypes.doPacket((short)136, var7);
      var7.putInt(var3);
      var7.putUTF(var1);
      if (var2.size() < var4) {
         var7.putInt(var2.size());
      } else if (var2.size() - var3 < var4) {
         var7.putInt(var2.size() - var3);
      } else {
         var7.putInt(var4);
      }

      for(int var8 = var3; var8 < var2.size(); ++var8) {
         DBResult var9 = null;

         try {
            var9 = (DBResult)var2.get(var8);
            var7.putInt(var9.getColumns().size());
         } catch (Exception var12) {
            var12.printStackTrace();
         }

         Iterator var10 = var9.getColumns().iterator();

         while(var10.hasNext()) {
            String var11 = (String)var10.next();
            var7.putUTF(var11);
            var7.putUTF((String)var9.getValues().get(var11));
         }

         ++var5;
         if (var5 >= var4) {
            var6 = false;
            var0.endPacketImmediate();
            doTableResult(var0, var1, var2, var3 + var5, var4);
            break;
         }
      }

      if (var6) {
         var0.endPacketImmediate();
      }

   }

   private static void executeQuery(ByteBuffer var0, UdpConnection var1) throws SQLException {
      if (var1.accessLevel != null && var1.accessLevel.equals("admin")) {
         try {
            String var2 = GameWindow.ReadString(var0);
            KahluaTable var3 = LuaManager.platform.newTable();
            var3.load(var0, 184);
            ServerWorldDatabase.instance.executeQuery(var2, var3);
         } catch (Throwable var4) {
            var4.printStackTrace();
         }

      }
   }

   private static void sendFactionInvite(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);
      String var3 = GameWindow.ReadString(var0);
      String var4 = GameWindow.ReadString(var0);
      IsoPlayer var5 = getPlayerByUserName(var4);
      Long var6 = (Long)IDToAddressMap.get(var5.getOnlineID());

      for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
         UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
         if (var8.getConnectedGUID() == var6) {
            ByteBufferWriter var9 = var8.startPacket();
            PacketTypes.doPacket((short)141, var9);
            var9.putUTF(var2);
            var9.putUTF(var3);
            var8.endPacketImmediate();
            break;
         }
      }

   }

   private static void acceptedFactionInvite(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);
      String var3 = GameWindow.ReadString(var0);
      IsoPlayer var4 = getPlayerByUserName(var3);
      Long var5 = (Long)IDToAddressMap.get(var4.getOnlineID());

      for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
         UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
         if (var7.getConnectedGUID() == var5) {
            Faction var8 = Faction.getPlayerFaction(var7.username);
            if (var8 != null && var8.getName().equals(var2)) {
               ByteBufferWriter var9 = var7.startPacket();
               PacketTypes.doPacket((short)142, var9);
               var9.putUTF(var2);
               var9.putUTF(var3);
               var7.endPacketImmediate();
            }
         }
      }

   }

   private static void viewTickets(ByteBuffer var0, UdpConnection var1) throws SQLException {
      String var2 = GameWindow.ReadString(var0);
      if ("".equals(var2)) {
         var2 = null;
      }

      sendTickets(var2, var1);
   }

   private static void sendTickets(String var0, UdpConnection var1) throws SQLException {
      ArrayList var2 = ServerWorldDatabase.instance.getTickets(var0);

      for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
         if (var4.getConnectedGUID() == var1.getConnectedGUID()) {
            ByteBufferWriter var5 = var4.startPacket();
            PacketTypes.doPacket((short)144, var5);
            var5.putInt(var2.size());

            for(int var6 = 0; var6 < var2.size(); ++var6) {
               DBTicket var7 = (DBTicket)var2.get(var6);
               var5.putUTF(var7.getAuthor());
               var5.putUTF(var7.getMessage());
               var5.putInt(var7.getTicketID());
               if (var7.getAnswer() != null) {
                  var5.putByte((byte)1);
                  var5.putUTF(var7.getAnswer().getAuthor());
                  var5.putUTF(var7.getAnswer().getMessage());
                  var5.putInt(var7.getAnswer().getTicketID());
               } else {
                  var5.putByte((byte)0);
               }
            }

            var4.endPacketImmediate();
            break;
         }
      }

   }

   private static void addTicket(ByteBuffer var0, UdpConnection var1) throws SQLException {
      String var2 = GameWindow.ReadString(var0);
      String var3 = GameWindow.ReadString(var0);
      int var4 = var0.getInt();
      if (var4 == -1) {
         sendAdminMessage("user " + var2 + " added a ticket <LINE> <LINE> " + var3, -1, -1, -1);
      }

      ServerWorldDatabase.instance.addTicket(var2, var3, var4);
      sendTickets(var2, var1);
   }

   private static void removeTicket(ByteBuffer var0, UdpConnection var1) throws SQLException {
      int var2 = var0.getInt();
      ServerWorldDatabase.instance.removeTicket(var2);
      sendTickets((String)null, var1);
   }

   public static boolean sendItemListNet(UdpConnection var0, IsoPlayer var1, ArrayList<InventoryItem> var2, IsoPlayer var3, String var4, String var5) {
      for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
         UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
         if (var0 == null || var7 != var0) {
            if (var3 != null) {
               boolean var8 = false;

               for(int var9 = 0; var9 < var7.players.length; ++var9) {
                  IsoPlayer var10 = var7.players[var9];
                  if (var10 != null && var10 == var3) {
                     var8 = true;
                     break;
                  }
               }

               if (!var8) {
                  continue;
               }
            }

            ByteBufferWriter var12 = var7.startPacket();
            PacketTypes.doPacket((short)150, var12);
            var12.putByte((byte)(var3 != null ? 1 : 0));
            if (var3 != null) {
               var12.putShort((short)var3.getOnlineID());
            }

            var12.putByte((byte)(var1 != null ? 1 : 0));
            if (var1 != null) {
               var12.putShort((short)var1.getOnlineID());
            }

            GameWindow.WriteString(var12.bb, var4);
            var12.putByte((byte)(var5 != null ? 1 : 0));
            if (var5 != null) {
               GameWindow.WriteString(var12.bb, var5);
            }

            try {
               CompressIdenticalItems.save(var12.bb, var2, (IsoGameCharacter)null);
            } catch (Exception var11) {
               var11.printStackTrace();
               var7.cancelPacket();
               return false;
            }

            var7.endPacketImmediate();
         }
      }

      return true;
   }

   private static void receiveItemListNet(ByteBuffer var0, UdpConnection var1) {
      IsoPlayer var2 = null;
      if (var0.get() == 1) {
         var2 = (IsoPlayer)IDToPlayerMap.get(var0.getShort());
      }

      IsoPlayer var3 = null;
      if (var0.get() == 1) {
         var3 = (IsoPlayer)IDToPlayerMap.get(var0.getShort());
      }

      String var4 = GameWindow.ReadString(var0);
      String var5 = null;
      if (var0.get() == 1) {
         var5 = GameWindow.ReadString(var0);
      }

      ArrayList var6 = new ArrayList();

      try {
         CompressIdenticalItems.load(var0, 184, var6, (ArrayList)null);
      } catch (Exception var8) {
         var8.printStackTrace();
      }

      if (var2 == null) {
         LuaEventManager.triggerEvent("OnReceiveItemListNet", var3, var6, var2, var4, var5);
      } else {
         sendItemListNet(var1, var3, var6, var2, var4, var5);
      }

   }

   public static void sendPlayerDamagedByCarCrash(IsoPlayer var0, float var1) {
      UdpConnection var2 = getConnectionFromPlayer(var0);
      if (var2 != null) {
         ByteBufferWriter var3 = var2.startPacket();
         PacketTypes.doPacket((short)172, var3);
         var3.putFloat(var1);
         var2.endPacketImmediate();
      }
   }

   private static void receiveClimateManagerPacket(ByteBuffer var0, UdpConnection var1) {
      ClimateManager var2 = ClimateManager.getInstance();
      if (var2 != null) {
         try {
            var2.receiveClimatePacket(var0, var1);
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }

   }

   private static String isWorldVersionUnsupported() {
      String var10002 = ZomboidFileSystem.instance.getSaveDir();
      File var0 = new File(var10002 + File.separator + "Multiplayer" + File.separator + ServerName + File.separator + "map_t.bin");
      if (var0.exists()) {
         DebugLog.log("checking server WorldVersion in map_t.bin");

         try {
            FileInputStream var1 = new FileInputStream(var0);

            String var8;
            label93: {
               label92: {
                  String var14;
                  label112: {
                     try {
                        DataInputStream var2 = new DataInputStream(var1);

                        label87: {
                           label86: {
                              label85: {
                                 try {
                                    byte var3 = var2.readByte();
                                    byte var4 = var2.readByte();
                                    byte var5 = var2.readByte();
                                    byte var6 = var2.readByte();
                                    if (var3 != 71 || var4 != 77 || var5 != 84 || var6 != 77) {
                                       var14 = "The server savefile appears to be from an old version of the game and cannot be loaded.";
                                       break label85;
                                    }

                                    int var7 = var2.readInt();
                                    if (var7 <= 184) {
                                       if (var7 > 143) {
                                          break label87;
                                       }

                                       var8 = "The server savefile appears to be from a pre-animations version of the game and cannot be loaded.\nDue to the extent of changes required to implement animations, saves from earlier versions are not compatible.";
                                       break label86;
                                    }

                                    var8 = "The server savefile appears to be from a newer version of the game and cannot be loaded.";
                                 } catch (Throwable var11) {
                                    try {
                                       var2.close();
                                    } catch (Throwable var10) {
                                       var11.addSuppressed(var10);
                                    }

                                    throw var11;
                                 }

                                 var2.close();
                                 break label93;
                              }

                              var2.close();
                              break label112;
                           }

                           var2.close();
                           break label92;
                        }

                        var2.close();
                     } catch (Throwable var12) {
                        try {
                           var1.close();
                        } catch (Throwable var9) {
                           var12.addSuppressed(var9);
                        }

                        throw var12;
                     }

                     var1.close();
                     return null;
                  }

                  var1.close();
                  return var14;
               }

               var1.close();
               return var8;
            }

            var1.close();
            return var8;
         } catch (Exception var13) {
            var13.printStackTrace();
         }
      } else {
         DebugLog.log("map_t.bin does not exist, cannot determine the server's WorldVersion.");
      }

      return null;
   }

   public String getPoisonousBerry() {
      return this.poisonousBerry;
   }

   public void setPoisonousBerry(String var1) {
      this.poisonousBerry = var1;
   }

   public String getPoisonousMushroom() {
      return this.poisonousMushroom;
   }

   public void setPoisonousMushroom(String var1) {
      this.poisonousMushroom = var1;
   }

   public String getDifficulty() {
      return this.difficulty;
   }

   public void setDifficulty(String var1) {
      this.difficulty = var1;
   }

   public static void receiveHitVehicle(ByteBuffer var0, UdpConnection var1) {
      HitVehicle var2 = new HitVehicle();
      var2.parse(var0);
      if (var2.check()) {
         for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
            UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
            if (var4.getConnectedGUID() != var1.getConnectedGUID() && var4.ReleventTo(var2.wielder.x, var2.wielder.y)) {
               ByteBufferWriter var5 = var4.startPacket();
               PacketTypes.doPacket((short)213, var5);
               var2.write(var5);
               var4.endPacketImmediate();
            }
         }

         DebugLog.log(DebugType.Multiplayer, "HitVehicle receive: " + var2.getDescription());
         if (var2.targetType == 1) {
            ((IsoZombie)var2.target).networkAI.hitVehicle = var2;
         }

      }
   }

   public static void transmitBrokenGlass(IsoGridSquare var0) {
      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);

         try {
            if (var2.ReleventTo((float)var0.getX(), (float)var0.getY())) {
               ByteBufferWriter var3 = var2.startPacket();
               PacketTypes.doPacket((short)175, var3);
               var3.putInt((short)var0.getX());
               var3.putInt((short)var0.getY());
               var3.putInt((short)var0.getZ());
               var2.endPacketImmediate();
            }
         } catch (Throwable var4) {
            var2.cancelPacket();
            ExceptionLogger.logException(var4);
         }
      }

   }

   public static boolean isServerDropPackets() {
      return droppedPackets > 0;
   }

   private static void receiveChrHitByVehicle(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      boolean var3 = var0.get() == 1;
      IsoPlayer var4 = getPlayerFromConnection(var1, var2);
      if (var4 != null) {
         for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
            UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
            if (var6.getConnectedGUID() != var1.getConnectedGUID()) {
               IsoPlayer var7 = getAnyPlayerFromConnection(var1);
               if (var7 != null) {
                  try {
                     ByteBufferWriter var8 = var6.startPacket();
                     PacketTypes.doPacket((short)176, var8);
                     var8.putShort((short)var4.OnlineID);
                     var8.putByte((byte)(var3 ? 1 : 0));
                     var6.endPacketImmediate();
                  } catch (Throwable var9) {
                     var1.cancelPacket();
                     ExceptionLogger.logException(var9);
                  }
               }
            }
         }

      }
   }

   private static void receiveSyncPerks(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      IsoPlayer var6 = getPlayerFromConnection(var1, var2);
      if (var6 != null) {
         var6.remoteSneakLvl = var3;
         var6.remoteStrLvl = var4;
         var6.remoteFitLvl = var5;

         for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
            if (var8.getConnectedGUID() != var1.getConnectedGUID()) {
               IsoPlayer var9 = getAnyPlayerFromConnection(var1);
               if (var9 != null) {
                  try {
                     ByteBufferWriter var10 = var8.startPacket();
                     PacketTypes.doPacket((short)177, var10);
                     var10.putShort((short)var6.OnlineID);
                     var10.putInt(var3);
                     var10.putInt(var4);
                     var10.putInt(var5);
                     var8.endPacketImmediate();
                  } catch (Throwable var11) {
                     var1.cancelPacket();
                     ExceptionLogger.logException(var11);
                  }
               }
            }
         }

      }
   }

   private static void receiveSyncWeight(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      float var3 = var0.getFloat();
      IsoPlayer var4 = getPlayerFromConnection(var1, var2);
      if (var4 != null) {
         for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
            UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
            if (var6.getConnectedGUID() != var1.getConnectedGUID()) {
               IsoPlayer var7 = getAnyPlayerFromConnection(var1);
               if (var7 != null) {
                  try {
                     ByteBufferWriter var8 = var6.startPacket();
                     PacketTypes.doPacket((short)178, var8);
                     var8.putShort((short)var4.OnlineID);
                     var8.putFloat(var3);
                     var6.endPacketImmediate();
                  } catch (Throwable var9) {
                     var1.cancelPacket();
                     ExceptionLogger.logException(var9);
                  }
               }
            }
         }

      }
   }

   private static void receiveSyncInjuries(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      float var3 = var0.getFloat();
      float var4 = var0.getFloat();
      IsoPlayer var5 = getPlayerFromConnection(var1, var2);
      if (var5 != null) {
         var5.setVariable("WalkSpeed", var3);
         var5.setVariable("WalkInjury", var4);

         for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
            UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
            if (var7.getConnectedGUID() != var1.getConnectedGUID()) {
               IsoPlayer var8 = getAnyPlayerFromConnection(var1);
               if (var8 != null) {
                  try {
                     ByteBufferWriter var9 = var7.startPacket();
                     PacketTypes.doPacket((short)179, var9);
                     var9.putShort((short)var5.OnlineID);
                     var9.putFloat(var3);
                     var9.putFloat(var4);
                     var7.endPacketImmediate();
                  } catch (Throwable var10) {
                     var1.cancelPacket();
                     ExceptionLogger.logException(var10);
                  }
               }
            }
         }

      }
   }

   private static void receiveSyncEquippedRadioFreq(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      int var3 = var0.getInt();
      ArrayList var4 = new ArrayList();

      for(int var5 = 0; var5 < var3; ++var5) {
         var4.add(var0.getInt());
      }

      IsoPlayer var12 = getPlayerFromConnection(var1, var2);
      if (var12 != null) {
         for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
            UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
            if (var7.getConnectedGUID() != var1.getConnectedGUID()) {
               IsoPlayer var8 = getAnyPlayerFromConnection(var1);
               if (var8 != null) {
                  try {
                     ByteBufferWriter var9 = var7.startPacket();
                     PacketTypes.doPacket((short)181, var9);
                     var9.putShort((short)var12.OnlineID);
                     var9.putInt(var3);

                     for(int var10 = 0; var10 < var4.size(); ++var10) {
                        var9.putInt((Integer)var4.get(var10));
                     }

                     var7.endPacketImmediate();
                  } catch (Throwable var11) {
                     var1.cancelPacket();
                     ExceptionLogger.logException(var11);
                  }
               }
            }
         }

      }
   }

   private static void receiveHitReactionFromZombie(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      String var3 = GameWindow.ReadStringUTF(var0);
      IsoPlayer var4 = getPlayerFromConnection(var1, var2);
      if (var4 != null) {
         for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
            UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
            if (var6.getConnectedGUID() != var1.getConnectedGUID()) {
               IsoPlayer var7 = getAnyPlayerFromConnection(var1);
               if (var7 != null) {
                  try {
                     ByteBufferWriter var8 = var6.startPacket();
                     PacketTypes.doPacket((short)180, var8);
                     var8.putShort((short)var4.OnlineID);
                     var8.putUTF(var3);
                     var6.endPacketImmediate();
                  } catch (Throwable var9) {
                     var1.cancelPacket();
                     ExceptionLogger.logException(var9);
                  }
               }
            }
         }

      }
   }

   private static void receiveGlobalModData(ByteBuffer var0, UdpConnection var1) {
      GlobalModData.instance.receive(var0);
   }

   private static void receiveGlobalModDataRequest(ByteBuffer var0, UdpConnection var1) {
      GlobalModData.instance.receiveRequest(var0, var1);
   }

   private static void sendSafehouseInvite(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);
      String var3 = GameWindow.ReadString(var0);
      String var4 = GameWindow.ReadString(var0);
      IsoPlayer var5 = getPlayerByUserName(var4);
      Long var6 = (Long)IDToAddressMap.get(var5.getOnlineID());
      int var7 = var0.getInt();
      int var8 = var0.getInt();
      int var9 = var0.getInt();
      int var10 = var0.getInt();

      for(int var11 = 0; var11 < udpEngine.connections.size(); ++var11) {
         UdpConnection var12 = (UdpConnection)udpEngine.connections.get(var11);
         if (var12.getConnectedGUID() == var6) {
            ByteBufferWriter var13 = var12.startPacket();
            PacketTypes.doPacket((short)193, var13);
            var13.putUTF(var2);
            var13.putUTF(var3);
            var13.putInt(var7);
            var13.putInt(var8);
            var13.putInt(var9);
            var13.putInt(var10);
            var12.endPacketImmediate();
            break;
         }
      }

   }

   private static void acceptedSafehouseInvite(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);
      String var3 = GameWindow.ReadString(var0);
      String var4 = GameWindow.ReadString(var0);
      IsoPlayer var5 = getPlayerByUserName(var3);
      Long var6 = (Long)IDToAddressMap.get(var5.getOnlineID());
      int var7 = var0.getInt();
      int var8 = var0.getInt();
      int var9 = var0.getInt();
      int var10 = var0.getInt();

      for(int var11 = 0; var11 < udpEngine.connections.size(); ++var11) {
         UdpConnection var12 = (UdpConnection)udpEngine.connections.get(var11);
         if (var12.getConnectedGUID() == var6) {
            ByteBufferWriter var13 = var12.startPacket();
            PacketTypes.doPacket((short)194, var13);
            var13.putUTF(var2);
            var13.putUTF(var3);
            var13.putUTF(var4);
            var13.putInt(var7);
            var13.putInt(var8);
            var13.putInt(var9);
            var13.putInt(var10);
            var12.endPacketImmediate();
         }
      }

   }

   static {
      discordBot = new DiscordBot(ServerName, (var0, var1) -> {
         ChatServer.getInstance().sendMessageFromDiscordToGeneralChat(var0, var1);
      });
      checksum = "";
      GameMap = "Muldraugh, KY";
      transactionIDMap = new HashMap();
      worldObjectsServerSyncReq = new ObjectsSyncRequests(false);
      ip = "127.0.0.1";
      count = 0;
      SlotToConnection = new UdpConnection[512];
      PlayerToAddressMap = new HashMap();
      alreadyRemoved = new ArrayList();
      SendZombies = 0;
      launched = false;
      consoleCommands = new ArrayList();
      MainLoopPlayerUpdate = new HashMap();
      MainLoopNetDataHighPrioritet = new ArrayList();
      MainLoopNetData = new ArrayList();
      MainLoopNetData2 = new ArrayList();
      playerToCoordsMap = new HashMap();
      playerMovedToFastMap = new HashMap();
      large_file_bb = ByteBuffer.allocate(3145728);
      previousSave = Calendar.getInstance().getTimeInMillis();
      droppedPackets = 0;
      countOfDroppedPackets = 0;
      countOfDroppedConnections = 0;
   }
}