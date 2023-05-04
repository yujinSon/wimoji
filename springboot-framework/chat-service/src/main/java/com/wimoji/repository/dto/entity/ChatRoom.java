package com.wimoji.repository.dto.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.wimoji.repository.dto.request.ChatRoomReq;

import lombok.Data;

@Data
@Document(collection = "chat_room")
public class ChatRoom {
	String id; // null
	int eid;
	String title;
	int participant;
	int limit;
	List<String> userList;
	List<Chat> content;

	public ChatRoom(ChatRoomReq chatRoomReq, String uid) {
		this.id = null;
		this.eid = chatRoomReq.getEid();
		this.title = chatRoomReq.getTitle();
		this.participant = 1;
		this.limit = chatRoomReq.getLimit();
		this.userList = new ArrayList<>();
		userList.add(uid);
		this.content = new ArrayList<>();
	}
}
