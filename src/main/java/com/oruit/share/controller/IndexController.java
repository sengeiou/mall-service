package com.oruit.share.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.oruit.common.utils.StringUtils;
import com.oruit.share.domain.TbBannerDO;
import com.oruit.share.domain.TbClassifyDO;
import com.oruit.share.domain.TbUser;
import com.oruit.share.service.GoodsService;
import com.oruit.share.service.TaobaoService;
import com.oruit.share.service.UserService;
import com.oruit.weixin.WxUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Controller
public class IndexController {

    @Autowired
    private GoodsService goodsService;

    @Value("${weixin.appId}")
    private String appId;

    @Value("${weixin.appSecret}")
    private String appSecret;

    @Value("${weixin.url}")
    private String NET_WEB;

    @Autowired
    private UserService userService;

    @RequestMapping("/MP_verify_saioSVJexjgyNclA.txt")
    public String verify(HttpServletRequest request,Model model) {
        return "verify";
    }

    @RequestMapping("/index")
    public String index(HttpServletRequest request,Model model) {

        JSONObject obj = goodsService.getGoodClassify(request);
        if(obj.get("code").equals("1000")){
            List<Map<String,Object>> list = JSONObject.parseObject(obj.get("data").toString(),List.class);

            List<Map<String,Object>> arr = new ArrayList<>();
            if(list.size()> 4){
               for(int i=0;i<4;i++){
                   arr.add(list.get(i));
               }
            }
            model.addAttribute("classifyList",arr);
            model.addAttribute("cid",list.get(0).get("cid"));
        }
        List<TbClassifyDO> tbClassifyDOList = goodsService.queryTbClassify(1);
        model.addAttribute("tbClassifyDOList",tbClassifyDOList);
        HashMap map = new HashMap();
        map.put("bannerType","1");
        List<TbBannerDO> tbBannerList = goodsService.queryTbBanner(map);
        model.addAttribute("tbBannerList",tbBannerList);
        return "dtk_index";
    }


    @RequestMapping("/classify")
    public String classify(HttpServletRequest request, Model model) {
        JSONObject obj = goodsService.getGoodClassify(request);
        if(obj.get("code").equals("1000")){
            List<Map<String,Object>> list = JSONObject.parseObject(obj.get("data").toString(),List.class);
            model.addAttribute("classifyList",list);
            model.addAttribute("cid",list.get(0).get("cid"));
        }
        return "classify";
    }


    @RequestMapping("/changeText")
    @ResponseBody
    public JSONObject changeText(HttpServletRequest request,Model model){
        String cid = request.getParameter("cid");
        JSONObject obj = goodsService.getGoodClassifyText(cid);
        return obj;
    }

    @RequestMapping("/mineIndex")
    public void mineIndex(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String inviteUrl = NET_WEB +"/mine";
        inviteUrl = URLEncoder.encode(inviteUrl, "utf-8");
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appId + "&redirect_uri=" + inviteUrl
                + "&response_type=code&scope=snsapi_userinfo#wechat_redirect";
        response.sendRedirect(url);
    }

    @RequestMapping("/mine")
    public String mine(HttpServletRequest request, Model model,HttpSession session,String code) {
        String open_id = session.getAttribute("open_id")!=null?
                session.getAttribute("open_id").toString():"";
        log.info("login code:{}",code);
        TbUser user = null;
        if (open_id == null) {
            System.out.println("系统开始，检查open_id=" +
                    session.getAttribute("open_id"));
            user = WxUtils.oppenIdInfo(code,appId,appSecret,session);
            open_id = (String)session.getAttribute("open_id");

            if (StringUtils.isNotEmpty(open_id)) {
                List<TbUser> tbUsers = userService.queryUser(user);
                if (tbUsers.size() != 0) {
                    log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    log.info("用户存在，则update");
                    userService.updateTbUser(user);
                } else {
                    log.info("用户不存在，则insert");
                    user.setCreateTime(new Date());
                    userService.insertTbUser(user);
                }
            }
            else {
                log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                log.info("open_id==null");
            }
        }
        String headPic = session.getAttribute("headPic").toString();
        String nickName = session.getAttribute("nickName").toString();

        model.addAttribute("headPic",headPic);
        model.addAttribute("headPic",nickName);
        return "mine";
    }

    @RequestMapping("/video")
    public String video(HttpServletRequest request, Model model) {
        model.addAttribute("videoUrl","https://m.baidu.com/sf/vsearch?pd=xsp&word=%E6%90%9E%E7%AC%91%E8%A7%86%E9%A2%91&tn=vsearch&sa=vs_ala_xsp_4660_n_1&lid=10137342229833437883&ms=1&atn=index");
        return "video_list";
    }


}
