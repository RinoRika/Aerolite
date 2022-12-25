package lain.mods.inputfix;

import java.util.Arrays;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.ModMetadata;

public class InputFixDummyContainer extends DummyModContainer
{

    public InputFixDummyContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = "Aerolite";
        meta.name = "Aerolite";
        meta.version = "1.8.9";
        meta.authorList = Arrays.asList("Stars", "Packet", "Ry4nnnnn", "Crazy");
        meta.description = "A minecraft hack mod based on FDPClient";
        meta.credits = "";
        meta.url = "aerolite.tk";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        return true;
    }

}