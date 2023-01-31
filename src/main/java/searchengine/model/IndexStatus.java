package searchengine.model;

public enum IndexStatus {
    LOAD,
    LOADED,
    INDEXING,
    INDEXED,
    FAILED;

    IndexStatus() {
    }
}
