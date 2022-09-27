package me.derp.quantum.features.modules.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.derp.quantum.event.events.Render2DEvent;
import me.derp.quantum.features.modules.Module;
import me.derp.quantum.util.Timer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TooBeeQueue extends Module {
    public TooBeeQueue() {
        super("2b2t Queue", "give you 2b2t queue as hud", Category.RENDER, true, false, false);
    }

    Timer timer = new Timer();
    String[] queueStatus = new String[] { "Not updated", "Not updated" };

    @Override
    public void onTick() {
        if (fullNullCheck()) return;
        if (timer.passed(30000)) {
            // REGULAR QUEUE
            new Thread(() -> {
                try {
                    HttpURLConnection request = (HttpURLConnection) new URL("https://2bqueue.info/*").openConnection();
                    request.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
                    request.connect();

                    JsonParser jp = new JsonParser(); //from gson
                    JsonElement root = jp.parse(new BufferedReader(new InputStreamReader(request.getInputStream()))); //Convert the input stream to a json element
                    JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
                    queueStatus[0] = rootobj.get("regular").getAsString(); //just grab the zipcode
                    queueStatus[1] = rootobj.get("prio").getAsString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            timer.reset();
        }
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        if (fullNullCheck()) return;
        mc.fontRenderer.drawString("Regular Queue: " + ChatFormatting.BOLD + queueStatus[0], 10, 10, new Color(255, 255, 255).getRGB());
        mc.fontRenderer.drawString("Priority Queue: " + ChatFormatting.BOLD + queueStatus[1], 10, 10 + mc.fontRenderer.FONT_HEIGHT + 2, new Color(255, 255, 255).getRGB());
    }
}