package rendering.Utility;

public final class FilesName {
    // static variable single_instance of type Singleton
    private static FilesName single_instance = null;

    private String folderPath;
    private String imageFile;

    public void setMinutiaeFile(String minutiaeFile) {
        this.minutiaeFile = minutiaeFile;
    }

    private String minutiaeFile;
    private String edgesFile;
    private String edgesSCCFile;
    
    private String skeletonData;
    private String boundedSkeletonData;
    
    public String getRidgesFile() {
        return ridgesFile;
    }

    private String ridgesFile;
    
    public String getRidgesColorFile() {
        return ridgesColorFile;
    }

    private String ridgesColorFile;

    public String getTrianglesFile() { return trianglesFile; }

    private String trianglesFile;

    public String getFolderPath() {
        return folderPath;
    }

    public String getImageFile() {
        return imageFile;
    }

    public String getMinutiaeFile() {
        return minutiaeFile;
    }

    public String getEdgesFile() {
        return edgesFile;
    }
    
    public String getEdgesSCCFile() {
        return edgesSCCFile;
    }

    public String getSkeletonData() {
        return skeletonData;
    }

    public String getBoundedSkeletonData() {
        return boundedSkeletonData;
    }


    // private constructor restricted to this class itself
    private FilesName(){}

    public void build(String folderPath, String imageFile, String minutiaeFile, String edgesFile, String edgesSCCFile, String skeletonData, String shrinkedSkeletonData, String ridgesFile, String ridgesColorFile, String trianglesFile) {
        this.folderPath = folderPath;
        this.imageFile = imageFile;
        this.minutiaeFile = minutiaeFile;
        this.edgesFile = edgesFile;
        this.edgesSCCFile = edgesSCCFile;
        this.skeletonData = skeletonData;
        this.boundedSkeletonData = shrinkedSkeletonData;
        this.ridgesFile = ridgesFile;
        this.ridgesColorFile = ridgesColorFile;
        this.trianglesFile = trianglesFile;

    }


    public void build(String folderPath, String imageFile, String minutiaeFile, String edgesFile, String skeletonData, String shrinkedSkeletonData)
    {
        this.folderPath = folderPath;
        this.imageFile = imageFile;
        this.minutiaeFile = minutiaeFile;
        this.edgesFile = edgesFile;
        this.skeletonData = skeletonData;
        this.boundedSkeletonData = shrinkedSkeletonData;
    }

    
    // static method to create instance of Singleton class
    public static FilesName getInstance()
    {
        if (single_instance == null)
            single_instance = new FilesName();

        return single_instance;
    }
}
