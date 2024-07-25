package com.mrthinkj.userservice.repository;

import com.mrthinkj.userservice.entity.Subscription;
import com.mrthinkj.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByPublisherIdAndSubscriberId(Long subscriberId, Long publisherId);
    @Query(value = "SELECT s.subscriber FROM Subscription s where s.publisher.id = :publisherId")
    Page<User> findSubscriberByPublisherId(Long publisherId, Pageable pageable);
    @Query(value = "SELECT s.publisher FROM Subscription s where s.subscriber.id = :subscriberId")
    Page<User> findPublisherBySubscriberId(Long publisherId, Pageable pageable);
    @Query(value = "select s.subscriber.id from Subscription s where s.publisher.id =:publisherId")
    List<Long> findAllSubscriberIdsByPublisherId(Long publisherId);
    @Query(value = "SELECT COUNT(*) FROM subscription WHERE publisher_id = :publisherId", nativeQuery = true)
    int countByPublisherId(@Param("publisherId") Long publisherId);
    int countBySubscriberId(Long subscriberId);
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM subscription WHERE subscriber_id = :subscriberId AND publisher_id = :publisherId",
            nativeQuery = true)
    boolean existsBySubscriberIdAndPublisherId(@Param("subscriberId") Long subscriberId, @Param("publisherId") Long publisherId);
}
