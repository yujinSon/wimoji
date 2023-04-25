package com.wimoji.service;

import com.mongodb.client.result.UpdateResult;
import com.wimoji.base.GeneralException;
import com.wimoji.base.constant.Code;
import com.wimoji.repository.Entity.Emoji;
import com.wimoji.repository.Entity.User;
import com.wimoji.repository.UserRepository;
import com.wimoji.repository.dto.response.EmojiGetRes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmojiService {
    private final UserRepository userRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 새로운 이모지 저장
     * @param uid
     * @param eid
     * @param content
     * @param latitude
     * @param longitude
     * @param dongCode
     */
    public void saveEmoji(String uid, String eid, String content, String latitude, String longitude, String dongCode){
        Criteria criteria  = Criteria.where("uid").is(uid);
        Update update = new Update();

        Emoji emoji = Emoji.builder()
                .eid(eid)
                .content(content)
                .dongCode(dongCode)
                .latitude(latitude)
                .longitude(longitude)
                .dongCode(dongCode)
                .build();

        update.push("emoji", emoji);
        mongoTemplate.updateFirst(Query.query(criteria), update, User.class);
    }

    /**
     * 이모지 수정
     * @param uid
     * @param order
     * @param content
     */
    public void modifyEmoji(String uid, String order, String eid, String content){
        Criteria criteria = Criteria.where("uid").is(uid);
        Query query = new Query(criteria);
        User document = mongoTemplate.findOne(query, User.class);

        //index가 emoji list size내에 있어야 update
        int intOrder = Integer.parseInt(order);
        if(intOrder >= 0 && intOrder<document.getEmoji().size()) {
            if(document.getEmoji().get(intOrder).getEid().equals(eid)){
                Update update = new Update().set("emoji" + "." + order + ".content", content);
                FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(false);
                mongoTemplate.findAndModify(query, update, options, User.class);
            }
            else
                throw new GeneralException(Code.NO_EMOJI);
        }
        else
            throw new GeneralException(Code.NO_EMOJI);
    }

    /**
     * 이모지 삭제
     * @param uid
     * @param order
     * @param eid
     */
    public void deleteEmoji(String uid, String order, String eid){
        Criteria criteria = Criteria.where("uid").is(uid);
        Query query = new Query(criteria);
        User document = mongoTemplate.findOne(query, User.class);

        //index가 emoji list size내에 있어야 delete
        int intOrder = Integer.parseInt(order);
        if(intOrder >= 0 && intOrder<document.getEmoji().size()) {
            if(document.getEmoji().get(intOrder).getEid().equals(eid)){
                document.getEmoji().remove(intOrder);
                mongoTemplate.save(document);
            }
            else
                throw new GeneralException(Code.NO_EMOJI);
        }
        else
            throw new GeneralException(Code.NO_EMOJI);
    }

    /**
     * 이모지 리스트 가져오기
     * @param uid
     * @return
     */
    public List<EmojiGetRes> getEmojiList(String uid){
        Criteria criteria = Criteria.where("uid").is(uid);
        Query query = new Query(criteria);
        // 다른 방식으로 해보려 했지만 실패한 코드
//        query.fields().include("emoji");
//        List<Emoji> emojiList = mongoTemplate.find(query, Emoji.class);
        User user = mongoTemplate.findOne(query, User.class);
        List<Emoji> emojiList = user.getEmoji();

        List<EmojiGetRes> emojiGetList = new ArrayList<>();
        if(emojiList != null) {
            for (int i = 0; i < emojiList.size(); i++) {
                Emoji emoji = emojiList.get(i);
                EmojiGetRes emojiGetRes = EmojiGetRes.builder()
                        .content(emoji.getContent())
                        .dongCode(emoji.getDongCode())
                        .eid(emoji.getEid())
                        .latitude(emoji.getLatitude())
                        .longitude(emoji.getLongitude())
                        .build();
                emojiGetList.add(emojiGetRes);
            }
        }

        return emojiGetList;
    }


}
