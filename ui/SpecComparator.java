package ui;

import ui.SpecInputFrame.UserSpecs;
import ui.SoftwareResultFrame.SoftwareCompatibility;

public class SpecComparator {
    
    public SoftwareCompatibility checkCompatibility(
            UserSpecs userSpecs, String softwareName,
            String reqOS, String reqProcessor, String reqRAM,
            String reqStorage, String reqGPU, String reqScreen) {
        
        SoftwareCompatibility comp = new SoftwareCompatibility();
        comp.softwareName = softwareName;
        comp.compatibilityScore = 0;
        int totalChecks = 0;

        // Check OS compatibility
        if (reqOS != null && !reqOS.trim().isEmpty()) {
            totalChecks++;
            comp.osMatch = reqOS;
            if (isOSCompatible(userSpecs.os, reqOS)) {
                comp.osCompatible = true;
                comp.compatibilityScore += 20;
            } else {
                comp.osCompatible = false;
            }
        }

        // Check Processor compatibility
        if (reqProcessor != null && !reqProcessor.trim().isEmpty() && 
            userSpecs.processor != null && !userSpecs.processor.trim().isEmpty()) {
            totalChecks++;
            comp.processorMatch = reqProcessor;
            if (isProcessorCompatible(userSpecs.processor, reqProcessor)) {
                comp.processorCompatible = true;
                comp.compatibilityScore += 20;
            } else {
                comp.processorCompatible = false;
                comp.compatibilityScore += 5; // Partial credit for having a processor
            }
        } else if (reqProcessor != null && !reqProcessor.trim().isEmpty()) {
            totalChecks++;
            comp.processorMatch = reqProcessor;
            comp.processorCompatible = false;
        }

        // Check RAM compatibility
        if (reqRAM != null && !reqRAM.trim().isEmpty()) {
            totalChecks++;
            comp.ramMatch = reqRAM;
            if (isRAMCompatible(userSpecs.ram, reqRAM)) {
                comp.ramCompatible = true;
                comp.compatibilityScore += 15;
            } else {
                comp.ramCompatible = false;
            }
        }

        // Check Storage compatibility
        if (reqStorage != null && !reqStorage.trim().isEmpty()) {
            totalChecks++;
            comp.storageMatch = reqStorage;
            if (isStorageCompatible(userSpecs.storage, reqStorage)) {
                comp.storageCompatible = true;
                comp.compatibilityScore += 15;
            } else {
                comp.storageCompatible = false;
            }
        }

        // Check GPU compatibility
        if (reqGPU != null && !reqGPU.trim().isEmpty() && 
            userSpecs.gpu != null && !userSpecs.gpu.trim().isEmpty()) {
            totalChecks++;
            comp.gpuMatch = reqGPU;
            if (isGPUCompatible(userSpecs.gpu, reqGPU)) {
                comp.gpuCompatible = true;
                comp.compatibilityScore += 15;
            } else {
                comp.gpuCompatible = false;
                comp.compatibilityScore += 5; // Partial credit for having a GPU
            }
        } else if (reqGPU != null && !reqGPU.trim().isEmpty()) {
            totalChecks++;
            comp.gpuMatch = reqGPU;
            comp.gpuCompatible = false;
        }

        // Check Screen compatibility (less critical)
        if (reqScreen != null && !reqScreen.trim().isEmpty()) {
            totalChecks++;
            comp.screenMatch = reqScreen;
            if (isScreenCompatible(userSpecs.screen, reqScreen)) {
                comp.screenCompatible = true;
                comp.compatibilityScore += 15;
            } else {
                comp.screenCompatible = false;
                comp.compatibilityScore += 10; // Partial credit as screen is less critical
            }
        }

        // Normalize score to percentage (max 100)
        if (totalChecks > 0) {
            int maxPossibleScore = Math.min(100, totalChecks * 20);
            comp.compatibilityScore = (comp.compatibilityScore * 100) / maxPossibleScore;
        }

        return comp;
    }

    private boolean isOSCompatible(String userOS, String reqOS) {
        if (userOS == null || reqOS == null) return false;
        
        String userOSLower = userOS.toLowerCase();
        String reqOSLower = reqOS.toLowerCase();

        // Windows compatibility
        if (userOSLower.contains("windows") && reqOSLower.contains("windows")) {
            if (userOSLower.contains("11") && reqOSLower.contains("11")) return true;
            if (userOSLower.contains("10") && (reqOSLower.contains("10") || reqOSLower.contains("11"))) return true;
            if (userOSLower.contains("8") && reqOSLower.contains("windows")) return true;
            if (userOSLower.contains("7") && reqOSLower.contains("windows")) return true;
        }

        // macOS compatibility
        if (userOSLower.contains("mac") && reqOSLower.contains("mac")) return true;

        // Linux compatibility
        if (userOSLower.contains("linux") && reqOSLower.contains("linux")) return true;

        return false;
    }

    private boolean isProcessorCompatible(String userProcessor, String reqProcessor) {
        if (userProcessor == null || reqProcessor == null) return false;
        
        String userProc = userProcessor.toLowerCase();
        String reqProc = reqProcessor.toLowerCase();

        // Extract processor tier
        int userTier = getProcessorTier(userProc);
        int reqTier = getProcessorTier(reqProc);

        // User's processor should be at least as powerful as required
        return userTier >= reqTier;
    }

    private int getProcessorTier(String processor) {
        String proc = processor.toLowerCase();
        
        // Intel processors: i9 (highest) > i7 > i5 > i3
        if (proc.contains("i9") || proc.contains("core i9")) return 9;
        if (proc.contains("i7") || proc.contains("core i7")) return 7;
        if (proc.contains("i5") || proc.contains("core i5")) return 5;
        if (proc.contains("i3") || proc.contains("core i3")) return 3;

        // AMD Ryzen processors: Ryzen 9 > Ryzen 7 > Ryzen 5 > Ryzen 3
        if (proc.contains("ryzen 9") || proc.contains("r9")) return 9;
        if (proc.contains("ryzen 7") || proc.contains("r7")) return 7;
        if (proc.contains("ryzen 5") || proc.contains("r5")) return 5;
        if (proc.contains("ryzen 3") || proc.contains("r3")) return 3;

        // Default tier for unknown processors
        return 4;
    }

    private boolean isRAMCompatible(String userRAM, String reqRAM) {
        if (userRAM == null || reqRAM == null) return false;
        
        int userRAMGB = extractRAM(userRAM);
        int reqRAMGB = extractRAM(reqRAM);

        return userRAMGB >= reqRAMGB;
    }

    private int extractRAM(String ram) {
        if (ram == null) return 0;
        
        String ramLower = ram.toLowerCase().replaceAll("\\s+", "");
        
        // Try to extract number in GB
        if (ramLower.contains("64gb") || ramLower.contains("64")) return 64;
        if (ramLower.contains("32gb") || ramLower.contains("32")) return 32;
        if (ramLower.contains("16gb") || ramLower.contains("16")) return 16;
        if (ramLower.contains("8gb") || ramLower.contains("8")) return 8;
        if (ramLower.contains("4gb") || ramLower.contains("4")) return 4;
        if (ramLower.contains("2gb") || ramLower.contains("2")) return 2;

        return 0;
    }

    private boolean isStorageCompatible(String userStorage, String reqStorage) {
        if (userStorage == null || reqStorage == null) return false;
        
        int userStorageGB = extractStorage(userStorage);
        int reqStorageGB = extractStorage(reqStorage);

        return userStorageGB >= reqStorageGB;
    }

    private int extractStorage(String storage) {
        if (storage == null) return 0;
        
        String storageLower = storage.toLowerCase().replaceAll("\\s+", "");
        
        // Convert to GB
        if (storageLower.contains("4tb") || storageLower.contains("4000gb")) return 4000;
        if (storageLower.contains("2tb") || storageLower.contains("2000gb")) return 2000;
        if (storageLower.contains("1tb") || storageLower.contains("1000gb")) return 1000;
        if (storageLower.contains("512gb") || storageLower.contains("512")) return 512;
        if (storageLower.contains("256gb") || storageLower.contains("256")) return 256;
        if (storageLower.contains("128gb") || storageLower.contains("128")) return 128;
        if (storageLower.contains("64gb") || storageLower.contains("64")) return 64;

        return 0;
    }

    private boolean isGPUCompatible(String userGPU, String reqGPU) {
        if (userGPU == null || reqGPU == null) return false;
        
        String userGPULower = userGPU.toLowerCase();
        String reqGPULower = reqGPU.toLowerCase();

        // If required GPU is "integrated" or "none", any GPU should work
        if (reqGPULower.contains("integrated") || reqGPULower.contains("none")) {
            return true;
        }

        // If user has integrated and required is dedicated, not compatible
        if (userGPULower.contains("integrated") && !reqGPULower.contains("integrated")) {
            return false;
        }

        // Extract GPU tier
        int userGPUTier = getGPUTier(userGPULower);
        int reqGPUTier = getGPUTier(reqGPULower);

        // User's GPU should be at least as powerful as required
        return userGPUTier >= reqGPUTier;
    }

    private int getGPUTier(String gpu) {
        String gpuLower = gpu.toLowerCase();
        
        // RTX 40 series
        if (gpuLower.contains("rtx 4090")) return 40;
        if (gpuLower.contains("rtx 4080")) return 39;
        if (gpuLower.contains("rtx 4070")) return 38;
        if (gpuLower.contains("rtx 4060")) return 37;

        // RTX 30 series
        if (gpuLower.contains("rtx 3090")) return 35;
        if (gpuLower.contains("rtx 3080")) return 34;
        if (gpuLower.contains("rtx 3070")) return 33;
        if (gpuLower.contains("rtx 3060")) return 32;

        // RTX 20 series
        if (gpuLower.contains("rtx 2080")) return 28;
        if (gpuLower.contains("rtx 2070")) return 27;
        if (gpuLower.contains("rtx 2060")) return 26;

        // GTX series
        if (gpuLower.contains("gtx 1660")) return 16;
        if (gpuLower.contains("gtx 1080")) return 18;
        if (gpuLower.contains("gtx 1070")) return 17;
        if (gpuLower.contains("gtx 1060")) return 16;
        if (gpuLower.contains("gtx")) return 15;

        // Integrated graphics
        if (gpuLower.contains("integrated")) return 5;

        // Default for unknown GPUs
        return 10;
    }

    private boolean isScreenCompatible(String userScreen, String reqScreen) {
        if (userScreen == null || reqScreen == null) return false;
        
        double userScreenSize = extractScreenSize(userScreen);
        double reqScreenSize = extractScreenSize(reqScreen);

        // Screen size compatibility is less strict - user's screen can be smaller
        // but it's acceptable (just might be less comfortable)
        return userScreenSize >= reqScreenSize * 0.8; // 80% of required size is acceptable
    }

    private double extractScreenSize(String screen) {
        if (screen == null) return 0;
        
        String screenLower = screen.toLowerCase();
        
        // Extract screen size in inches
        if (screenLower.contains("17")) return 17.0;
        if (screenLower.contains("15.6") || screenLower.contains("15")) return 15.6;
        if (screenLower.contains("14")) return 14.0;
        if (screenLower.contains("13")) return 13.0;
        if (screenLower.contains("12")) return 12.0;
        if (screenLower.contains("11")) return 11.0;

        return 0;
    }
}

