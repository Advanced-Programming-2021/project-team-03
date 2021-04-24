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

    public String importCard(String cardName) {
        //TODO
        return null;
    }

    public String exportCard(String cardName) {
        //TODO
        return null;
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
