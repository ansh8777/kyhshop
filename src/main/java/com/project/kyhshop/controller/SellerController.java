package com.project.kyhshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import com.project.kyhshop.dao.*;

@Controller
public class SellerController {
    @Autowired
    SellerDao sd;
    
    @GetMapping("/seller/product")
    public String productList() {
        return "/html/seller/productlist";
    }
}
