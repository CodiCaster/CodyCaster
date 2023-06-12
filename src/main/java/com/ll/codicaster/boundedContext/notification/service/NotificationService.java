package com.ll.codicaster.boundedContext.notification.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.member.entity.Member;
import com.ll.codicaster.boundedContext.notification.entity.Notification;
import com.ll.codicaster.boundedContext.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

	private final NotificationRepository notificationRepository;

	public List<Notification> findByReceiver(Member receiver) {
		List<Notification> notifications = notificationRepository.findByReceiver(receiver);

		return notifications.stream()
			.sorted(Comparator.comparing(Notification::getId).reversed())
			.collect(Collectors.toList());
	}

	@Transactional
	public RsData<Notification> makeLike(Article article, String typeCode, Member actor) {
		Notification notification = Notification
			.builder()
			.article(article)
			.typeCode(typeCode)
			.actor(actor)
			.createDate(LocalDateTime.now())
			.receiver(article.getAuthor())
			.build();
		notificationRepository.save(notification);
		return RsData.of("S-1", "알림 메세지가 생성되었습니다.", notification);
	}

	@Transactional
	public RsData<Notification> makeFollow(Member follower, Member followee, String typeCode) {
		Notification notification = Notification
			.builder()
			.article(null)
			.typeCode(typeCode)
			.actor(follower)
			.receiver(followee)
			.build();
		notificationRepository.save(notification);
		return RsData.of("S-1", "알림 메세지가 생성되었습니다.", notification);
	}

	@Transactional
	public RsData markAsRead(List<Notification> notifications) {
		notifications
			.stream()
			.filter(notification -> !notification.isRead())
			.forEach(Notification::markAsRead);

		return RsData.of("S-1", "읽음 처리 되었습니다.");
	}

	public boolean countUnreadNotificationsByReceiver(Member receiver) {
		return notificationRepository.countByReceiverAndReadDateIsNull(receiver) > 0;
	}

}
