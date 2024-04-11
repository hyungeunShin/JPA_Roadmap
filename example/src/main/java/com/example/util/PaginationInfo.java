package com.example.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
public class PaginationInfo<T> {
	private int totalRecord;
	private int totalPage;
	private int currentPage;
	private int screenSize = 3;
	private int blockSize = 3;
	private int startRow;
	private int endRow;
	private int startPage;
	private int endPage;
	private List<T> dataList;
	private String searchType;
	private String searchWord;
	
	public PaginationInfo() {
		
	}
	
	public PaginationInfo(int screenSize, int blockSize) {
		this.screenSize = screenSize;
		this.blockSize = blockSize;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
		
		totalPage = (int) Math.ceil(totalRecord / (double) screenSize);
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
		endRow = currentPage * screenSize;
		startRow = endRow - (screenSize - 1);
		
		endPage = (currentPage + (blockSize - 1)) / blockSize * blockSize;
		startPage = endPage - (blockSize - 1);
	}

	public String getPagingHTML() {
		StringBuffer html = new StringBuffer();
		
		html.append("<ul class='pagination pagination-sm m-0 float-right'>");
		
		if(startPage > 1) {
			html.append("<li class='page-item'><a href='' class='page-link' data-page='" + (startPage - blockSize) + "'>Prev</a></li>");
		}
		
		for(int i = startPage; i <= (endPage < totalPage ? endPage : totalPage); i++) {
			if(i == currentPage) {
				html.append("<li class='page-item active'><span class='page-link'>" + i + "</span></li>");
			} else {
				html.append("<li class='page-item'><a href='' class='page-link' data-page='" + i + "'>" + i + "</a></li>");
			}
		}
		
		if(endPage < totalPage) {
			html.append("<li class='page-item'><a href='' class='page-link' data-page='" + (endPage + 1) + "'>Next</a></li>");
		}
		
		html.append("</ul>");
		
		return html.toString();
	}
}