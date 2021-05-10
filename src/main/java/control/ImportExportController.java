package control;

public class ImportExportController {

    private static ImportExportController importExportController;

    private ImportExportController() {
    }

    public static ImportExportController getInstance() {
        if (importExportController == null)
            importExportController = new ImportExportController();
        return importExportController;
    }

    public void importCard(String cardName) {
        //TODO
    }

    public void exportCard(String cardName) {
        //TODO
    }

    public boolean canImportThisCard(String cardName) {
        //TODO
        return false;
    }

    public boolean canExportThisCard(String cardName) {
        //TODO
        return false;
    }
}
