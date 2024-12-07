package com.example.stock.controller;

import com.example.stock.domain.MarketCategory;
import com.example.stock.dto.StockDetailDTO;
import com.example.stock.dto.StockListDTO;
import com.example.stock.service.StockService;
import com.example.util.PaginationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StockController {
    private final StockService service;

    @ModelAttribute("searchType")
    public MarketCategory[] searchType() {
        return MarketCategory.values();
    }

    @GetMapping("/stock/list")
    public String stockList(@RequestParam(name = "page", required = false, defaultValue = "1") int currentPage,
                            @RequestParam(name = "searchType", required = false) String searchType,
                            @RequestParam(name = "searchWord", required = false) String searchWord,
                            Model model) {
        PaginationInfo<StockListDTO> paginationInfo = new PaginationInfo<>();

        if(StringUtils.hasText(searchWord)) {
            paginationInfo.setSearchWord(searchWord);
            model.addAttribute("searchWord", searchWord);
        }

        if(StringUtils.hasText(searchType)) {
            paginationInfo.setSearchType(searchType);
            //model.addAttribute("searchType", searchType);
        }

        paginationInfo.setCurrentPage(currentPage);

        int totalRecord = service.getStockTotalCount(paginationInfo);
        paginationInfo.setTotalCount(totalRecord);

        List<StockListDTO> dataList = service.getStockList(paginationInfo);
        paginationInfo.setDataList(dataList);

        model.addAttribute("paginationInfo", paginationInfo);

        return "stock/list";
    }

    @GetMapping("/stock/detail")
    public String stockDetail(@RequestParam("isinCd") String isinCd, RedirectAttributes ra, Model model) {
        List<StockDetailDTO> result = service.getChartData(isinCd);

        if(result.isEmpty()) {
            ra.addFlashAttribute("message", "존재하지 않는 주식입니다.");
            return "redirect:/stock/list";
        }

        model.addAttribute("itemName", result.get(0).getItemName());
        model.addAttribute("response", result);
        return "stock/detail";
    }
}
