package com.jfpuhui.anti.web.controller;

import com.jfpuhui.anti.common.MD5Utils;
import com.jfpuhui.anti.common.Profile;
import com.jfpuhui.anti.exception.CustomException;
import com.jfpuhui.anti.service.GraphService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.security.provider.MD5;

import java.util.HashMap;
import java.util.Map;

/**搜索
 * @author Nisus-Liu
 * @version 1.0.0
 * @email liuhejun108@163.com
 * @date 2018-03-22-13:59
 */
@Controller
@Slf4j
public class SearchController {


    @Autowired
    private GraphService graphService;




    @RequestMapping(value = {"/search"})  //http://localhost:8080/certno/110101195607302022.do
    @ResponseBody
    public Map<String, Object> searchGraphByVid(String searchType, String searchContent) throws CustomException {
        long t0 = System.currentTimeMillis();
        searchType = StringUtils.trim(searchType);
        searchContent = StringUtils.trim(searchContent);

        if (StringUtils.isBlank(searchType) || StringUtils.isBlank(searchContent)) {
            throw new CustomException("搜索条件为空");
        }

        log.debug("Request param [searchType]:{}, [searchContent]: {} ",searchType, searchContent);

        Map<String, Object> graph = new HashMap<>();

        switch (searchType) {
            case "身份证号":
                graph=graphService.selectByCertNo4Smart(searchContent);
                break;
            case "姓名":
                graph=graphService.selectByName4Smart(searchContent);
                break;
            case "客户类型":
                graph=graphService.selectByCustType4Smart(Profile.CustType.matchDescToCode(searchContent));
                break;
            default:    //剩下都是根据边查找     计算出md5 然后根据 vid查找
                String vid = MD5Utils.md5(searchType + searchContent);
                graph = graphService.selectByVid4Smart(vid);
        }


        log.info("Number of node: " + (((Map) graph.get("nodes")) == null ? 0 : ((Map) graph.get("nodes")).size()));
        log.info("Number of edge: " + (((Map) graph.get("edges"))==null?0:((Map) graph.get("edges")).size()));

        long t1 = System.currentTimeMillis();
        log.info("Request processing time: " + (t1 - t0) + "ms");
        return graph;
    }


}
