package model;

public class Software {
    private String name;
    private String os;
    private String processor;
    private String ram;
    private String storage;
    private String gpu;
    private String screen;

    public Software(String name, String os, String processor, String ram,
                    String storage, String gpu, String screen) {
        this.name = name;
        this.os = os;
        this.processor = processor;
        this.ram = ram;
        this.storage = storage;
        this.gpu = gpu;
        this.screen = screen;
    }

    public String getName() { return name; }
    public String getOs() { return os; }
    public String getProcessor() { return processor; }
    public String getRam() { return ram; }
    public String getStorage() { return storage; }
    public String getGpu() { return gpu; }
    public String getScreen() { return screen; }
}
