package com.ll.codicaster.boundedContext.follow;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.codicaster.base.event.EventBeforeFollow;
import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.boundedContext.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 아래 메서드들이 전부 readonly 라는 것을 명시, 나중을 위해
public class FollowService {
	private final FollowRepository followRepository;
	private final ApplicationEventPublisher publisher;
	// 특정 멤버가 다른 멤버를 팔로우하는 기능

	public RsData whenBeforeFollow(Member follower, Long articleId){
		publisher.publishEvent(new EventBeforeFollow(this,follower, articleId));
		return RsData.of("S-1","팔로우에 성공하였습니다.");
	}

	public RsData followMember(Member followee, Member follower) {

		Follow follow = Follow
			.builder()
			.follower(follower)
			.followee(followee)
			.build();
		followRepository.save(follow);

		return RsData.of("S-1", "팔로우에 성공했습니다.");
	}

	// 특정 멤버가 다른 멤버를 언팔로우하는 기능
	public void unfollowMember(Member follower, Member followee) {
		Follow follow = followRepository.findByFollowerAndFollowee(follower, followee);
		if (follow != null) {
			followRepository.delete(follow);
		}
	}

	// 특정 멤버가 팔로우하는 멤버 목록을 조회하는 기능
	public List<Member> getFollowingMembers(Member follower) {
		List<Follow> follows = followRepository.findByFollower(follower);
		return follows.stream().map(Follow::getFollowee).collect(Collectors.toList());
	}

	// 특정 멤버를 팔로우하는 멤버 목록을 조회하는 기능
	public List<Member> getFollowers(Member followee) {
		List<Follow> follows = followRepository.findByFollowee(followee);
		return follows.stream().map(Follow::getFollower).collect(Collectors.toList());
	}

	public boolean isFollowed(Member follower, Member followee) {
		List<Member> followingMembers = getFollowingMembers(follower);
		return followingMembers.contains(followee);
	}


}