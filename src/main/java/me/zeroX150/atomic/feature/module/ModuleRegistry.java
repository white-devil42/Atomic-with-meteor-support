/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module;

import me.zeroX150.atomic.feature.module.impl.client.Alts;
import me.zeroX150.atomic.feature.module.impl.client.ClientConfig;
import me.zeroX150.atomic.feature.module.impl.combat.AimAssist;
import me.zeroX150.atomic.feature.module.impl.combat.ArmorSwitch;
import me.zeroX150.atomic.feature.module.impl.combat.AutoEndermanAngry;
import me.zeroX150.atomic.feature.module.impl.combat.AutoLog;
import me.zeroX150.atomic.feature.module.impl.combat.Criticals;
import me.zeroX150.atomic.feature.module.impl.combat.Killaura;
import me.zeroX150.atomic.feature.module.impl.combat.ProtectFriends;
import me.zeroX150.atomic.feature.module.impl.combat.Velocity;
import me.zeroX150.atomic.feature.module.impl.exploit.AntiAntiXray;
import me.zeroX150.atomic.feature.module.impl.exploit.AntiOffhandCrash;
import me.zeroX150.atomic.feature.module.impl.exploit.AntiPacketKick;
import me.zeroX150.atomic.feature.module.impl.exploit.AntiReducedDebugInfo;
import me.zeroX150.atomic.feature.module.impl.exploit.BoatPhase;
import me.zeroX150.atomic.feature.module.impl.exploit.Boaty;
import me.zeroX150.atomic.feature.module.impl.exploit.CaveMapper;
import me.zeroX150.atomic.feature.module.impl.exploit.HologramAura;
import me.zeroX150.atomic.feature.module.impl.exploit.NoComCrash;
import me.zeroX150.atomic.feature.module.impl.exploit.OOBCrash;
import me.zeroX150.atomic.feature.module.impl.exploit.OffhandCrash;
import me.zeroX150.atomic.feature.module.impl.exploit.Phase;
import me.zeroX150.atomic.feature.module.impl.exploit.PingSpoof;
import me.zeroX150.atomic.feature.module.impl.exploit.SoundLogger;
import me.zeroX150.atomic.feature.module.impl.exploit.VanillaSpoof;
import me.zeroX150.atomic.feature.module.impl.exploit.VerticalPhase;
import me.zeroX150.atomic.feature.module.impl.fun.BHop;
import me.zeroX150.atomic.feature.module.impl.fun.Deadmau5;
import me.zeroX150.atomic.feature.module.impl.fun.NWordCounter;
import me.zeroX150.atomic.feature.module.impl.fun.Physics;
import me.zeroX150.atomic.feature.module.impl.fun.SpinAutism;
import me.zeroX150.atomic.feature.module.impl.misc.AllowFormatCodes;
import me.zeroX150.atomic.feature.module.impl.misc.AutoLogin;
import me.zeroX150.atomic.feature.module.impl.misc.ChatSequence;
import me.zeroX150.atomic.feature.module.impl.misc.DiscordRPC;
import me.zeroX150.atomic.feature.module.impl.misc.InfChatLength;
import me.zeroX150.atomic.feature.module.impl.misc.InventoryCleaner;
import me.zeroX150.atomic.feature.module.impl.misc.MCF;
import me.zeroX150.atomic.feature.module.impl.misc.NameProtect;
import me.zeroX150.atomic.feature.module.impl.misc.NoTitles;
import me.zeroX150.atomic.feature.module.impl.misc.PortalGUI;
import me.zeroX150.atomic.feature.module.impl.misc.SlotSpammer;
import me.zeroX150.atomic.feature.module.impl.misc.TexPackSpoof;
import me.zeroX150.atomic.feature.module.impl.misc.Timer;
import me.zeroX150.atomic.feature.module.impl.misc.UsefulInfoLogger;
import me.zeroX150.atomic.feature.module.impl.misc.WindowCustomization;
import me.zeroX150.atomic.feature.module.impl.misc.XCarry;
import me.zeroX150.atomic.feature.module.impl.movement.AirJump;
import me.zeroX150.atomic.feature.module.impl.movement.AntiVoid;
import me.zeroX150.atomic.feature.module.impl.movement.ArrowJuke;
import me.zeroX150.atomic.feature.module.impl.movement.AutoElytra;
import me.zeroX150.atomic.feature.module.impl.movement.AutoWalk;
import me.zeroX150.atomic.feature.module.impl.movement.Blink;
import me.zeroX150.atomic.feature.module.impl.movement.Boost;
import me.zeroX150.atomic.feature.module.impl.movement.ClickFly;
import me.zeroX150.atomic.feature.module.impl.movement.EdgeJump;
import me.zeroX150.atomic.feature.module.impl.movement.EdgeSneak;
import me.zeroX150.atomic.feature.module.impl.movement.EntityFly;
import me.zeroX150.atomic.feature.module.impl.movement.Flight;
import me.zeroX150.atomic.feature.module.impl.movement.IgnoreWorldBorder;
import me.zeroX150.atomic.feature.module.impl.movement.InventoryWalk;
import me.zeroX150.atomic.feature.module.impl.movement.Jesus;
import me.zeroX150.atomic.feature.module.impl.movement.LongJump;
import me.zeroX150.atomic.feature.module.impl.movement.MoonGravity;
import me.zeroX150.atomic.feature.module.impl.movement.NoFall;
import me.zeroX150.atomic.feature.module.impl.movement.NoJumpCooldown;
import me.zeroX150.atomic.feature.module.impl.movement.NoPush;
import me.zeroX150.atomic.feature.module.impl.movement.Speed;
import me.zeroX150.atomic.feature.module.impl.movement.Sprint;
import me.zeroX150.atomic.feature.module.impl.movement.Squake;
import me.zeroX150.atomic.feature.module.impl.movement.Step;
import me.zeroX150.atomic.feature.module.impl.render.Animations;
import me.zeroX150.atomic.feature.module.impl.render.BetterCrosshair;
import me.zeroX150.atomic.feature.module.impl.render.ChestESP;
import me.zeroX150.atomic.feature.module.impl.render.CleanGUI;
import me.zeroX150.atomic.feature.module.impl.render.ClickGUI;
import me.zeroX150.atomic.feature.module.impl.render.CommandBlockPreview;
import me.zeroX150.atomic.feature.module.impl.render.ESP;
import me.zeroX150.atomic.feature.module.impl.render.EntityFullbright;
import me.zeroX150.atomic.feature.module.impl.render.EntitySpawnInfo;
import me.zeroX150.atomic.feature.module.impl.render.FreeLook;
import me.zeroX150.atomic.feature.module.impl.render.Freecam;
import me.zeroX150.atomic.feature.module.impl.render.Fullbright;
import me.zeroX150.atomic.feature.module.impl.render.Hud;
import me.zeroX150.atomic.feature.module.impl.render.ItemByteSize;
import me.zeroX150.atomic.feature.module.impl.render.NameTags;
import me.zeroX150.atomic.feature.module.impl.render.NoRender;
import me.zeroX150.atomic.feature.module.impl.render.TabGUI;
import me.zeroX150.atomic.feature.module.impl.render.TargetHud;
import me.zeroX150.atomic.feature.module.impl.render.Tracers;
import me.zeroX150.atomic.feature.module.impl.render.Waypoints;
import me.zeroX150.atomic.feature.module.impl.render.Zoom;
import me.zeroX150.atomic.feature.module.impl.render.oreSim.OreSim;
import me.zeroX150.atomic.feature.module.impl.testing.Debugger;
import me.zeroX150.atomic.feature.module.impl.testing.TestModule;
import me.zeroX150.atomic.feature.module.impl.world.AutoCone;
import me.zeroX150.atomic.feature.module.impl.world.AutoRepeater;
import me.zeroX150.atomic.feature.module.impl.world.AutoTool;
import me.zeroX150.atomic.feature.module.impl.world.BlockSpammer;
import me.zeroX150.atomic.feature.module.impl.world.BlockTagViewer;
import me.zeroX150.atomic.feature.module.impl.world.Bunker;
import me.zeroX150.atomic.feature.module.impl.world.ClickNuke;
import me.zeroX150.atomic.feature.module.impl.world.FarmingAura;
import me.zeroX150.atomic.feature.module.impl.world.FastUse;
import me.zeroX150.atomic.feature.module.impl.world.Flattener;
import me.zeroX150.atomic.feature.module.impl.world.GodBridge;
import me.zeroX150.atomic.feature.module.impl.world.InstantBreak;
import me.zeroX150.atomic.feature.module.impl.world.LeverAura;
import me.zeroX150.atomic.feature.module.impl.world.MassFillNuke;
import me.zeroX150.atomic.feature.module.impl.world.MidAirPlace;
import me.zeroX150.atomic.feature.module.impl.world.NoBreakDelay;
import me.zeroX150.atomic.feature.module.impl.world.Nuker;
import me.zeroX150.atomic.feature.module.impl.world.Scaffold;
import me.zeroX150.atomic.feature.module.impl.world.Tunnel;
import me.zeroX150.atomic.feature.module.impl.world.WaterClutch;
import me.zeroX150.atomic.feature.module.impl.world.XRAY;
import me.zeroX150.atomic.helper.font.FontRenderers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModuleRegistry {

    static final List<Module> modules = new ArrayList<>();

    static {
        modules.add(new TestModule());
        modules.add(new ClickGUI());
        modules.add(new AirJump());
        modules.add(new ArrowJuke());
        modules.add(new EdgeSneak());
        modules.add(new Blink());
        modules.add(new EntityFly());
        modules.add(new Hud());
        modules.add(new Boost());
        modules.add(new Flight());
        modules.add(new Jesus());
        modules.add(new MoonGravity());
        modules.add(new NoFall());
        modules.add(new NoJumpCooldown());
        modules.add(new ClickFly());
        modules.add(new Speed());
        modules.add(new Sprint());
        modules.add(new Step());
        modules.add(new AutoLog());
        modules.add(new OreSim());
        modules.add(new Nuker());
        modules.add(new Criticals());
        modules.add(new Killaura());
        modules.add(new AntiAntiXray());
        modules.add(new XRAY());
        modules.add(new AntiOffhandCrash());
        modules.add(new BoatPhase());
        modules.add(new SoundLogger());
        modules.add(new PingSpoof());
        modules.add(new AntiPacketKick());
        modules.add(new Fullbright());
        modules.add(new NameTags());
        modules.add(new ClientConfig());
        modules.add(new Tracers());
        modules.add(new ESP());
        modules.add(new Alts());
        modules.add(new HologramAura());
        modules.add(new TexPackSpoof());
        modules.add(new Bunker());
        modules.add(new SlotSpammer());
        modules.add(new VerticalPhase());
        modules.add(new Freecam());
        modules.add(new NoPush());
        modules.add(new WaterClutch());
        modules.add(new Zoom());
        modules.add(new AutoEndermanAngry());
        modules.add(new MidAirPlace());
        modules.add(new InventoryWalk());
        modules.add(new TargetHud());
        modules.add(new FarmingAura());
        modules.add(new BetterCrosshair());
        modules.add(new NoBreakDelay());
        modules.add(new ChestESP());
        modules.add(new InventoryCleaner());
        modules.add(new OffhandCrash());
        modules.add(new BlockSpammer());
        modules.add(new NoRender());
        modules.add(new VanillaSpoof());
        modules.add(new Scaffold());
        modules.add(new AntiVoid());
        modules.add(new Phase());
        modules.add(new NameProtect());
        modules.add(new LeverAura());
        modules.add(new ChatSequence());
        modules.add(new GodBridge());
        modules.add(new AntiReducedDebugInfo());
        modules.add(new FastUse());
        modules.add(new AutoCone());
        modules.add(new CleanGUI());
        modules.add(new Timer());
        modules.add(new FreeLook());
        modules.add(new ClickNuke());
        modules.add(new MassFillNuke());
        modules.add(new AutoTool());
        modules.add(new NWordCounter());
        modules.add(new AutoLogin());
        modules.add(new Boaty());
        modules.add(new NoComCrash());
        modules.add(new UsefulInfoLogger());
        modules.add(new InfChatLength());
        modules.add(new DiscordRPC());
        modules.add(new PortalGUI());
        modules.add(new BHop());
        modules.add(new IgnoreWorldBorder());
        modules.add(new AutoWalk());
        modules.add(new WindowCustomization());
        modules.add(new InstantBreak());
        modules.add(new Velocity());
        modules.add(new AutoRepeater());
        modules.add(new EntitySpawnInfo());
        modules.add(new CommandBlockPreview());
        modules.add(new BlockTagViewer());
        modules.add(new Waypoints());
        modules.add(new AimAssist());
        modules.add(new MCF());
        modules.add(new ProtectFriends());
        modules.add(new Tunnel());
        modules.add(new Animations());
        modules.add(new AutoElytra());
        modules.add(new ArmorSwitch());
        modules.add(new Deadmau5());
        modules.add(new Squake());
        modules.add(new OOBCrash());
        modules.add(new TabGUI());
        modules.add(new NoTitles());
        modules.add(new AllowFormatCodes());
        modules.add(new Debugger());
        modules.add(new CaveMapper());
        modules.add(new SpinAutism());
        modules.add(new EntityFullbright());
        modules.add(new EdgeJump());
        modules.add(new LongJump());
        modules.add(new Flattener());
        modules.add(new ItemByteSize());
        modules.add(new Physics());
        modules.add(new XCarry());
    }

    public static void sortModulesPostInit() {
        modules.sort(Comparator.comparingDouble(value -> -FontRenderers.normal.getStringWidth(value.getName())));
    }

    public static boolean isDebuggerEnabled() {
        return getByClass(Debugger.class).isEnabled();
    }

    public static Debugger getDebugger() {
        return getByClass(Debugger.class);
    }

    public static List<Module> getModules() {
        return modules;
    }

    @SuppressWarnings("unchecked") public static <T extends Module> T getByClass(Class<T> clazz) {
        for (Module module : getModules()) {
            if (module.getClass() == clazz) {
                return (T) module;
            }
        }
        throw new IllegalStateException("Unregistered module");
    }

    public static Module getByName(String n) {
        for (Module module : getModules()) {
            if (module.getName().equalsIgnoreCase(n)) {
                return module;
            }
        }
        return null;
    }
}
