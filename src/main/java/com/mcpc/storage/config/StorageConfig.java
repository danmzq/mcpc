package com.mcpc.storage.config;

import java.util.ArrayList;
import java.util.List;

public class StorageConfig {
    public List<SourceChest> sources = new ArrayList<>();
    public List<DestinationChest> destinations = new ArrayList<>();

    public static class SourceChest {
        public String id;
        public int[] pos;
    }

    public static class DestinationChest {
        public String id;
        public int[] pos;
        public List<String> items = new ArrayList<>();
    }
}
