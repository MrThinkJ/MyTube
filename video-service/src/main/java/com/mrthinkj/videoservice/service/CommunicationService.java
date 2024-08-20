package com.mrthinkj.videoservice.service;

public interface CommunicationService {
    void sendNewVideoNotificationToSubscribers(String videoUUID);
    void sendNewVideoInfoForSearchEngine(String videoUUID);
}
