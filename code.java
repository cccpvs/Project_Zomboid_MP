      ByteBuffer var1 = var0.buffer;
      UdpConnection var2 = udpEngine.getActiveConnection(var0.connection);
      if (var0.type >= 0 && var0.type < packetCounts.length) {
         int var10002 = packetCounts[var0.type]++;
         if (var2 != null) {
            var10002 = var2.packetCounts[var0.type]++;
         }
      }

      try {
         if (var2 == null) {
            DebugLog.log(DebugType.Network, "Received packet type=" + PacketTypes.packetTypeToString(var0.type) + " connection is null.");
            return;
         }

         if (var2.username == null) {
            switch(var0.type) {
            case 2:
            case 50:
            case 87:
               break;
            default:
               DebugLog.log("Received packet type=" + PacketTypes.packetTypeToString(var0.type) + " before Login, disconnecting " + var2.getInetSocketAddress().getHostString());
               var2.forceDisconnect();
               ZomboidNetDataPool.instance.discard(var0);
               return;
            }
         }

         String var19;
         label359:
         switch(var0.type) {
         case 2:
            String var16 = GameWindow.ReadString(var0.buffer).trim();
            String var18 = GameWindow.ReadString(var0.buffer).trim();
            var19 = GameWindow.ReadString(var0.buffer).trim();
            ByteBufferWriter var20;
            if (!var19.equals(Core.getInstance().getVersionNumber())) {
               var20 = var2.startPacket();
               PacketTypes.doPacket((short)40, var20);
               LoggerManager.getLogger("user").write("access denied: user \"" + var16 + "\" client version (" + var19 + ") does not match server version (" + Core.getInstance().getVersionNumber() + ")");
               var20.putUTF("ClientVersionMismatch##" + var19 + "##" + Core.getInstance().getVersionNumber());
               var2.endPacketImmediate();
               var2.forceDisconnect();
            }

            var2.ip = var2.getInetSocketAddress().getHostString();
            var2.idStr = var2.ip;
            if (SteamUtils.isSteamModeEnabled()) {
               var2.steamID = udpEngine.getClientSteamID(var2.getConnectedGUID());
               var2.ownerID = udpEngine.getClientOwnerSteamID(var2.getConnectedGUID());
               var2.idStr = SteamUtils.convertSteamIDToString(var2.steamID);
               if (var2.steamID != var2.ownerID) {
                  var2.idStr = var2.idStr + "(owner=" + SteamUtils.convertSteamIDToString(var2.ownerID) + ")";
               }
            }

            var2.password = var18;
            LoggerManager.getLogger("user").write(var2.idStr + " \"" + var16 + "\" attempting to join");
            LogonResult var21;
            if (CoopSlave.instance != null && SteamUtils.isSteamModeEnabled()) {
               for(int var22 = 0; var22 < udpEngine.connections.size(); ++var22) {
                  UdpConnection var26 = (UdpConnection)udpEngine.connections.get(var22);
                  if (var26 != var2 && var26.steamID == var2.steamID) {
                     LoggerManager.getLogger("user").write("access denied: user \"" + var16 + "\" already connected");
                     ByteBufferWriter var25 = var2.startPacket();
                     PacketTypes.doPacket((short)40, var25);
                     var25.putUTF("AlreadyConnected");
                     var2.endPacketImmediate();
                     var2.forceDisconnect();
                     return;
                  }
               }

               var2.username = var16;
               var2.usernames[0] = var16;
               var2.isCoopHost = udpEngine.connections.size() == 1;
               DebugLog.log(var2.idStr + " isCoopHost=" + var2.isCoopHost);
               var2.accessLevel = "";
               if (!ServerOptions.instance.DoLuaChecksum.getValue() || var2.accessLevel.equals("admin")) {
                  var2.checksumState = ChecksumState.Done;
               }

               if (getPlayerCount() >= ServerOptions.instance.MaxPlayers.getValue()) {
                  var20 = var2.startPacket();
                  PacketTypes.doPacket((short)40, var20);
                  var20.putUTF("ServerFull");
                  var2.endPacketImmediate();
                  var2.forceDisconnect();
                  return;
               }

               LoggerManager.getLogger("user").write(var2.idStr + " \"" + var16 + "\" allowed to join");
               ServerWorldDatabase var27 = ServerWorldDatabase.instance;
               var27.getClass();
               var21 = new LogonResult(var27);
               var21.accessLevel = var2.accessLevel;
               receiveClientConnect(var2, var21);
            } else {
               var21 = ServerWorldDatabase.instance.authClient(var16, var18, var2.ip, var2.steamID);
               ByteBufferWriter var24;
               if (var21.bAuthorized) {
                  for(int var9 = 0; var9 < udpEngine.connections.size(); ++var9) {
                     UdpConnection var10 = (UdpConnection)udpEngine.connections.get(var9);

                     for(int var11 = 0; var11 < 4; ++var11) {
                        if (var16.equals(var10.usernames[var11])) {
                           LoggerManager.getLogger("user").write("access denied: user \"" + var16 + "\" already connected");
                           ByteBufferWriter var12 = var2.startPacket();
                           PacketTypes.doPacket((short)40, var12);
                           var12.putUTF("AlreadyConnected");
                           var2.endPacketImmediate();
                           var2.forceDisconnect();
                           return;
                        }
                     }
                  }

                  var2.username = var16;
                  var2.usernames[0] = var16;
                  transactionIDMap.put(var16, var21.transactionID);
                  if (CoopSlave.instance != null) {
                     var2.isCoopHost = udpEngine.connections.size() == 1;
                     DebugLog.log(var2.idStr + " isCoopHost=" + var2.isCoopHost);
                  }

                  var2.accessLevel = var21.accessLevel;
                  if (!ServerOptions.instance.DoLuaChecksum.getValue() || var21.accessLevel.equals("admin")) {
                     var2.checksumState = ChecksumState.Done;
                  }

                  if (!var21.accessLevel.equals("") && getPlayerCount() >= ServerOptions.instance.MaxPlayers.getValue()) {
                     var24 = var2.startPacket();
                     PacketTypes.doPacket((short)40, var24);
                     var24.putUTF("ServerFull");
                     var2.endPacketImmediate();
                     var2.forceDisconnect();
                     return;
                  }

                  if (!ServerWorldDatabase.instance.containsUser(var16) && ServerWorldDatabase.instance.containsCaseinsensitiveUser(var16)) {
                     var24 = var2.startPacket();
                     PacketTypes.doPacket((short)40, var24);
                     var24.putUTF("InvalidUsername");
                     var2.endPacketImmediate();
                     var2.forceDisconnect();
                     return;
                  }

                  LoggerManager.getLogger("user").write(var2.idStr + " \"" + var16 + "\" allowed to join");
                  if (ServerOptions.instance.AutoCreateUserInWhiteList.getValue() && !ServerWorldDatabase.instance.containsUser(var16)) {
                     ServerWorldDatabase.instance.addUser(var16, var18);
                  } else {
                     ServerWorldDatabase.instance.setPassword(var16, var18);
                  }

                  ServerWorldDatabase.instance.updateLastConnectionDate(var16, var18);
                  if (SteamUtils.isSteamModeEnabled()) {
                     String var23 = SteamUtils.convertSteamIDToString(var2.steamID);
                     ServerWorldDatabase.instance.setUserSteamID(var16, var23);
                  }

                  receiveClientConnect(var2, var21);
               } else {
                  var24 = var2.startPacket();
                  PacketTypes.doPacket((short)40, var24);
                  if (var21.banned) {
                     LoggerManager.getLogger("user").write("access denied: user \"" + var16 + "\" is banned");
                     if (var21.bannedReason != null && !var21.bannedReason.isEmpty()) {
                        var24.putUTF("BannedReason##" + var21.bannedReason);
                     } else {
                        var24.putUTF("Banned");
                     }
                  } else if (!var21.bAuthorized) {
                     LoggerManager.getLogger("user").write("access denied: user \"" + var16 + "\" reason \"" + var21.dcReason + "\"");
                     var24.putUTF(var21.dcReason != null ? var21.dcReason : "AccessDenied");
                  }

                  var2.endPacketImmediate();
                  var2.forceDisconnect();
               }
            }
            break;
         case 3:
            receiveVisual(var1, var2);
            break;
         case 4:
            MPDebugInfo.instance.serverPacket(var1, var2);
            break;
         case 5:
            VehicleManager.instance.serverPacket(var1, var2);
            break;
         case 6:
            receivePlayerConnect(var1, var2, var2.username);
            sendInitialWorldState(var2);
            break;
         case 7:
            receivePlayerUpdate(var1, var2);
            break;
         case 11:
            sendHelicopter(var1, var2);
            break;
         case 12:
            SyncIsoObject(var1, var2);
            break;
         case 14:
            byte var15 = var1.get();
            switch(var15) {
            case 0:
               byte var17 = var1.get();
               var19 = GameWindow.ReadStringUTF(var1);
               IsoPlayer var8 = getPlayerFromConnection(var2, var17);
               if (var8 != null) {
                  SteamGameServer.BUpdateUserData(var8.getSteamID(), var8.username, 0);
               }
            default:
               break label359;
            }
         case 16:
            PassengerMap.serverReceivePacket(var1, var2);
            break;
         case 17:
            AddItemToMap(var1, var2);
            break;
         case 20:
            sendItemsToContainer(var1, var2);
            break;
         case 22:
            removeItemFromContainer(var1, var2);
            break;
         case 23:
            RemoveItemFromMap(var1, var2);
            break;
         case 24:
            if (var2.playerDownloadServer != null) {
               int var5 = var1.getInt();
               int var6 = var1.getInt();
               int var7 = var1.getInt();
               var2.connectArea[0] = new Vector3((float)var5, (float)var6, (float)var7);
               var2.ChunkGridWidth = var7;
               ZombiePopulationManager.instance.updateLoadedAreas();
            }
            break;
         case 25:
            equip(var1, var2);
            break;
         case 26:
            hitCharacter(var1, var2);
            break;
         case 27:
            addCoopPlayer(var1, var2);
            break;
         case 28:
            receiveWeaponHit(var1, var2);
            break;
         case 31:
            receiveSandboxOptions(var1);
            break;
         case 32:
            IsoObject var3 = IsoWorld.instance.getItemFromXYZIndexBuffer(var1);
            if (var3 != null && var3 instanceof IsoWindow) {
               byte var14 = var1.get();
               if (var14 == 1) {
                  ((IsoWindow)var3).smashWindow(true);
                  smashWindow((IsoWindow)var3, 1);
               } else if (var14 == 2) {
                  ((IsoWindow)var3).setGlassRemoved(true);
                  smashWindow((IsoWindow)var3, 2);
               }
            }
            break;
         case 33:
            receivePlayerDeath(var1, var2);
            break;
         case 34:
            if (var2.playerDownloadServer != null) {
               var2.playerDownloadServer.receiveRequestArray(var1);
            }
            break;
         case 35:
            receiveItemStats(var1, var2);
            break;
         case 36:
            if (var2.playerDownloadServer != null) {
               var2.playerDownloadServer.receiveCancelRequest(var1);
            }
            break;
         case 37:
            receiveRequestData(var1, var2);
            break;
         case 38:
            receiveGlobalObjects(var1, var2);
            break;
         case 39:
            receiveDeadZombie(var1, var2);
            break;
         case 42:
            doBandage(var1, var2);
            break;
         case 43:
            eatFood(var1, var2);
            break;
         case 44:
            requestItemsForContainer(var1, var2);
            break;
         case 45:
            drink(var1, var2);
            break;
         case 46:
            SyncAlarmClock(var1, var2);
            break;
         case 47:
            receivePacketCounts(var1, var2);
            break;
         case 48:
            loadModData(var1, var2);
            break;
         case 49:
            removeItemFromContainer(var1, var2);
            break;
         case 50:
            scoreboard(var2);
            break;
         case 51:
            loadModData(var1, var2);
            break;
         case 53:
            receiveSound(var1, var2);
            break;
         case 54:
            receiveWorldSound(var1);
            break;
         case 56:
            receiveClothing(var1, var2);
            break;
         case 57:
            receiveClientCommand(var1, var2);
            break;
         case 58:
            receiveObjectModData(var1, var2);
            break;
         case 65:
            SyncPlayerInventory(var1, var2);
            break;
         case 67:
            RequestPlayerData(var1, var2);
            break;
         case 68:
            removeCorpseFromMap(var1, var2);
            break;
         case 69:
            addCorpseToMap(var1, var2);
            break;
         case 75:
            startFireOnClient(var1, var2);
            break;
         case 76:
            updateItemSprite(var1, var2);
            break;
         case 79:
            sendWorldMessage(var1, var2);
            break;
         case 80:
            sendCustomModDataToClient(var2);
            break;
         case 81:
            ReceiveCommand(var1, var2);
            break;
         case 84:
            receivePlayerExtraInfo(var1, var2);
            break;
         case 86:
            toggleSafety(var1, var2);
            break;
         case 87:
            var2.ping = true;
            answerPing(var1, var2);
            break;
         case 88:
            log(var1, var2);
            break;
         case 90:
            updateOverlayFromClient(var1, var2);
            break;
         case 91:
            NetChecksum.comparer.serverPacket(var1, var2);
            break;
         case 92:
            constructedZone(var1, var2);
            break;
         case 94:
            registerZone(var1, var2);
            break;
         case 97:
            doWoundInfection(var1, var2);
            break;
         case 98:
            doStitch(var1, var2);
            break;
         case 99:
            doDisinfect(var1, var2);
            break;
         case 100:
            doAdditionalPain(var1, var2);
            break;
         case 101:
            doRemoveGlass(var1, var2);
            break;
         case 102:
            doSplint(var1, var2);
            break;
         case 103:
            doRemoveBullet(var1, var2);
            break;
         case 104:
            doCleanBurn(var1, var2);
            break;
         case 105:
            SyncThumpable(var1, var2);
            break;
         case 106:
            SyncDoorKey(var1, var2);
            break;
         case 108:
            teleport(var1, var2);
            break;
         case 109:
            removeBlood(var1, var2);
            break;
         case 110:
            AddExplosiveTrap(var1, var2);
            break;
         case 112:
            receiveBodyDamageUpdate(var1, var2);
            break;
         case 114:
            syncSafehouse(var1, var2);
            break;
         case 115:
            destroy(var1, var2);
            break;
         case 116:
            stopFire(var1, var2);
            break;
         case 117:
            doCataplasm(var1, var2);
            break;
         case 120:
            receiveFurnaceChange(var1, var2);
            break;
         case 121:
            receiveCustomColor(var1, var2);
            break;
         case 122:
            syncCompost(var1, var2);
            break;
         case 123:
            receivePlayerStatsChanges(var1, var2);
            break;
         case 124:
            addXpFromPlayerStatsUI(var1, var2);
            break;
         case 126:
            syncXp(var1, var2);
            break;
         case 127:
            dealWithNetDataShort(var0, var1, var2);
            break;
         case 128:
            sendUserlog(var1, var2, GameWindow.ReadString(var1));
            break;
         case 129:
            addUserlog(var1, var2);
            break;
         case 130:
            removeUserlog(var1, var2);
            break;
         case 131:
            addWarningPoint(var1, var2);
            break;
         case 133:
            wakeUpPlayer(var1, var2);
            break;
         case 134:
            receiveTransactionID(var1, var2);
            break;
         case 135:
            sendDBSchema(var1, var2);
            break;
         case 136:
            sendTableResult(var1, var2);
            break;
         case 137:
            executeQuery(var1, var2);
            break;
         case 138:
            receiveTextColor(var1, var2);
            break;
         case 139:
            syncNonPvpZone(var1, var2);
            break;
         case 140:
            syncFaction(var1, var2);
            break;
         case 141:
            sendFactionInvite(var1, var2);
            break;
         case 142:
            acceptedFactionInvite(var1, var2);
            break;
         case 143:
            addTicket(var1, var2);
            break;
         case 144:
            viewTickets(var1, var2);
            break;
         case 145:
            removeTicket(var1, var2);
            break;
         case 146:
            requestTrading(var1, var2);
            break;
         case 147:
            tradingUIAddItem(var1, var2);
            break;
         case 148:
            tradingUIRemoveItem(var1, var2);
            break;
         case 149:
            tradingUIUpdateState(var1, var2);
            break;
         case 150:
            receiveItemListNet(var1, var2);
            break;
         case 151:
            receiveChunkObjectState(var1, var2);
            break;
         case 152:
            readAnnotedMap(var1, var2);
            break;
         case 153:
            requestInventory(var1, var2);
            break;
         case 154:
            sendInventory(var1, var2);
            break;
         case 155:
            invMngSendItem(var1, var2);
            break;
         case 156:
            invMngGotItem(var1, var2);
            break;
         case 157:
            invMngRemoveItem(var1, var2);
            break;
         case 160:
            GameTime.getInstance();
            GameTime.receiveTimeSync(var1, var2);
            break;
         case 161:
            SyncIsoObjectReq(var1, var2);
            break;
         case 162:
            receivePlayerSave(var1, var2);
            break;
         case 164:
            short var4 = var1.getShort();
            if (var4 == 1) {
               SyncObjectChunkHashes(var1, var2);
            } else if (var4 == 3) {
               SyncObjectsGridSquareRequest(var1, var2);
            } else if (var4 == 5) {
               SyncObjectsRequest(var1, var2);
            }
            break;
         case 165:
            receivePlayerOnBeaten(var1, var2);
            break;
         case 166:
            receiveSendPlayerProfile(var1, var2);
            break;
         case 167:
            receiveLoadPlayerProfile(var1, var2);
            break;
         case 173:
            receiveAttachedItem(var1, var2);
            break;
         case 174:
            receiveZombieHelmetFalling(var1, var2);
            break;
         case 176:
            receiveChrHitByVehicle(var1, var2);
            break;
         case 177:
            receiveSyncPerks(var1, var2);
            break;
         case 178:
            receiveSyncWeight(var1, var2);
            break;
         case 179:
            receiveSyncInjuries(var1, var2);
            break;
         case 180:
            receiveHitReactionFromZombie(var1, var2);
            break;
         case 181:
            receiveSyncEquippedRadioFreq(var1, var2);
            break;
         case 185:
            ChatServer.getInstance().processMessageFromPlayerPacket(var1);
            break;
         case 187:
            ChatServer.getInstance().processPlayerStartWhisperChatPacket(var1);
            break;
         case 193:
            sendSafehouseInvite(var1, var2);
            break;
         case 194:
            acceptedSafehouseInvite(var1, var2);
            break;
         case 200:
            receiveClimateManagerPacket(var1, var2);
            break;
         case 202:
            IsoRegions.receiveClientRequestFullDataChunks(var1, var2);
            break;
         case 210:
            receiveEventUpdate(var1, var2);
            break;
         case 211:
            receiveStatistic(var1, var2);
            break;
         case 212:
            receiveStatisticRequest(var1, var2);
            break;
         case 213:
            receiveHitVehicle(var1, var2);
            break;
         case 216:
            receiveZombieAttackTarget(var1);
            break;
         case 218:
            receivePlayerUpdate(var1, var2);
            break;
         default:
            DebugLog.log("[DEBUG]: This is a missing feature for the netcode type " + var0.type + " from user " + var2.username);
         }
      } catch (Exception var13) {
         if (var2 == null) {
            DebugLog.log(DebugType.Network, "Error with packet of type: " + var0.type + " connection is null.");
         } else {
            DebugLog.log(DebugType.Network, "Error with packet of type: " + var0.type + " for " + var2.username);
         }

         var13.printStackTrace();
      }

      ZomboidNetDataPool.instance.discard(var0);
   }
