package smarthome.behavioral;

public interface Command {
    void execute();
    void undo();
    String describe();
}