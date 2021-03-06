package com.jessie.campusmutualassist.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jessie.campusmutualassist.entity.Vote;
import com.jessie.campusmutualassist.mapper.VoteMapper;
import com.jessie.campusmutualassist.service.VoteService;
import com.jessie.campusmutualassist.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.jessie.campusmutualassist.service.impl.PermissionServiceImpl.getCurrentUsername;

/**
 *
 */
@Service
public class VoteServiceImpl extends ServiceImpl<VoteMapper, Vote>
        implements VoteService {
    @Autowired
    VoteMapper voteMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    @CacheEvict(value = "ClassVotes", key = "#vote.classID+'*'")
    public void newVote(Vote vote) {
        voteMapper.newVote(vote);
    }

    @Override
    @Cacheable(value = "ClassVotes", key = "#classID+'-'+#pageNum")
    public PageInfo<Vote> getClassVotesPage(String classID, int pageNum) {
        PageHelper.startPage(pageNum, 10, "vid desc");
        List<Vote> list = voteMapper.getClassVotes(classID);//结果应该要逆序的....这样可以吗？
        for (Vote vote : list) {
            vote.setSelections(redisUtil.zReverseRange("class:" + classID + ":" + "type:" + "VoteSelections" + ":" + "vid:" + vote.getVid(), 0, -1));
        }
        return new PageInfo<Vote>(list);
    }

    @Override
    @Cacheable(value = "ClassVotes", key = "#classID")
    public List<Vote> getClassVotes(String classID) {
        List<Vote> list = voteMapper.getClassVotes(classID);//结果应该要逆序的....这样可以吗？
        for (Vote vote : list) {
            vote.setSelections(redisUtil.zReverseRange("class:" + classID + ":" + "type:" + "VoteSelections" + ":" + "vid:" + vote.getVid(), 0, -1));

            if (!redisUtil.sIsMember("class:" + classID + ":" + "type:" + "Voter" + ":" + "vid" + vote.getVid(), getCurrentUsername())) {
                vote.setVoted(false);
            } else {
                vote.setVoted(true);
            }
        }
        return list;
    }

    @Override
    @Cacheable(value = "voteByVid", key = "#vid")
    public Vote getVote(long vid) {
        return voteMapper.getVote(vid);
    }

    @Override
    public List<Long> getNotVotes(String username, String classID) {
        Long[] vids = voteMapper.getClassVotesID(classID);
        ArrayList<Long> arrayList = new ArrayList();
        for (long vid : vids) {
            if (!redisUtil.sIsMember("class:" + classID + ":" + "type:" + "Voter" + ":" + "vid" + vid, username)) {
                arrayList.add(vid);
            }
        }
        return arrayList;
    }

    @Override
    public void deleteVote(String classID, long vid) {
        redisUtil.delete("class:" + classID + ":" + "type:" + "Voter" + ":" + "vid:" + vid);
        redisUtil.delete(redisUtil.keys("class:" + classID + ":" + "type:" + "Voter" + ":" + "vid:" + vid + "*"));
        voteMapper.deleteVote(vid);
    }
}




