package com.mrthinkj.searchservice.service;

public interface VideoSyncService {
    void initialSync();
    void consistencyCheck();
}
