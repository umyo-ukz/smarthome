package smarthome.behavioral;

import java.util.ArrayDeque;
import java.util.Deque;

public class CommandInvoker {
    private final Deque<Command> history = new ArrayDeque<>();

    public void run(Command c) {
        c.execute();
        history.push(c);
        System.out.println("[Invoker] " + c.describe());
    }

    public void undoLast() {
        if (history.isEmpty()) {
            System.out.println("[Invoker] Nothing to undo");
            return;
        }
        Command c = history.pop();
        c.undo();
        System.out.println("[Invoker] Undid: " + c.describe());
    }

    public Deque<Command> getHistory() {
        return new ArrayDeque<>(history);
    }
}