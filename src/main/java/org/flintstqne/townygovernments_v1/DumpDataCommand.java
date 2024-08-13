package org.flintstqne.townygovernments_v1;

public class DumpDataCommand {
    private DatabaseManager databaseManager;
    private EconomicSystemManager economicSystemManager;
    private GovernmentSystemManager governmentSystemManager;
    private ChatHandler chatHandler;

    public DumpDataCommand(DatabaseManager databaseManager, EconomicSystemManager economicSystemManager, GovernmentSystemManager governmentSystemManager, ChatHandler chatHandler) {
        this.databaseManager = databaseManager;
        this.economicSystemManager = economicSystemManager;
        this.governmentSystemManager = governmentSystemManager;
        this.chatHandler = chatHandler;
    }

    public void execute() {
        try {
            databaseManager.dumpData();
            chatHandler.formatSuccess("Data dumped successfully.");
        } catch (Exception e) {
            chatHandler.formatError("An error occurred while dumping data.");
            e.printStackTrace();
        }
    }
}