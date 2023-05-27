package pers.qh.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.qh.service.BaseBrandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ConCurrentController {
    private final BaseBrandService brandService;
    @GetMapping("setNum")
    public String setNum(){
        brandService.setNum();
        return "success";
    }
}