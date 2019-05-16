package com.wskj.project.service.impl;

import com.wskj.project.dao.DBMapper;
import com.wskj.project.service.DBService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
@Service
public class DBServiceImpl implements DBService {

    @Resource
    private DBMapper dbMapper;
    @Override
    public boolean getUser(String userName) {
        boolean ok=true;
        Map<String,String> map=dbMapper.getUser(userName);
        if(map!=null){
            Object object = map.get("COUNT");
            int result=Integer.parseInt(String.valueOf(object ));
            if(result==0){
                ok=false;//不存在
            }
        }else{
            ok=false;
        }
        return ok;
    }
}
