package com.nightshadepvp.core.cmd.cmds.staff.effect;

import com.nightshadepvp.core.cmd.NightShadeCoreCommand;

import java.util.Collections;
import java.util.List;

public class CmdEffect extends NightShadeCoreCommand {

    private static CmdEffect i = new CmdEffect();
    public static CmdEffect get() {return i;}

    public CmdEffectAdd cmdEffectAdd = new CmdEffectAdd();
    public CmdEffectClear cmdEffectClear = new CmdEffectClear();
    public CmdEffectRemove cmdEffectRemove = new CmdEffectRemove();


    @Override
    public List<String> getAliases() {
        return Collections.singletonList("effect");
    }
}
