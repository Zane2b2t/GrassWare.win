package me.derp.quantum.manager;


<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> parent of 0c0a21a (Merge remote-tracking branch 'origin/main')
=======
>>>>>>> parent of 0c0a21a (Merge remote-tracking branch 'origin/main')
=======
>>>>>>> parent of 0c0a21a (Merge remote-tracking branch 'origin/main')
=======
>>>>>>> parent of 57bdf45 (Merge remote-tracking branch 'origin/main')
=======
>>>>>>> parent of 13274c9 (Removed .github stuff)
import me.derp.quantum.Quantum;
import me.derp.quantum.event.events.ThreadEvent;
import me.derp.quantum.features.modules.combat.TooBeeCrystalAura;
import me.derp.quantum.util.Multithread;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayDeque;
import java.util.Queue;

import static me.derp.quantum.features.Feature.nullCheck;

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
import java.util.ArrayDeque;
import java.util.Queue;

>>>>>>> parent of e1545ed (More)
=======
import java.util.ArrayDeque;
import java.util.Queue;

>>>>>>> parent of 509ddc3 (Revert "Removed .github stuff")
=======
>>>>>>> parent of 0c0a21a (Merge remote-tracking branch 'origin/main')
=======
import java.util.ArrayDeque;
import java.util.Queue;

>>>>>>> parent of 509ddc3 (Revert "Removed .github stuff")
=======
>>>>>>> parent of 0c0a21a (Merge remote-tracking branch 'origin/main')
=======
>>>>>>> parent of 0c0a21a (Merge remote-tracking branch 'origin/main')
=======
>>>>>>> parent of 57bdf45 (Merge remote-tracking branch 'origin/main')
=======
>>>>>>> parent of 13274c9 (Removed .github stuff)
=======
import java.util.ArrayDeque;
import java.util.Queue;

>>>>>>> parent of 8b2503e (More)
=======
import java.util.ArrayDeque;
import java.util.Queue;

>>>>>>> parent of e1545ed (More)
public class ThreadManager {

    // thread used by all modules
    private static final TooBeeCrystalAura ac = new TooBeeCrystalAura();
    private final ClientService clientService = new ClientService();
    private static final Queue<Runnable> clientProcesses = new ArrayDeque<>();

    public ThreadManager() {
        clientService.setDaemon(true);
        clientService.start();
    }

    public static class ClientService extends Thread {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    try {
                        Thread.yield();
                        // run and remove the latest service
                        if (clientProcesses.size() > 0) {
                            clientProcesses.poll().run();
                            clientProcesses.remove();
                        }
                    } catch (Exception exception) {
                        System.out.println("[noot logger]: thread exception wtf!!");
                        exception.printStackTrace();
                    }
                } catch (Exception exception) {
                    System.out.println("[noot logger]: thread exception wtf!!");
                }
            }
        }

        public void submit(Runnable in) {
            clientProcesses.add(in);
        }
    }
}
