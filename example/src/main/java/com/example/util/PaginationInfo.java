package com.example.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
public class PaginationInfo<T> {
	private int totalCount;
	private int totalPage;
	private int currentPage;
	private int pageSize = 20;
	private int blockSize = 5;
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
		this.pageSize = screenSize;
		this.blockSize = blockSize;
	}

	public void setTotalCount(int totalRecord) {
		this.totalCount = totalRecord;
		totalPage = (int) Math.ceil(totalRecord / (double) pageSize);
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;

		this.startRow = (currentPage - 1) * pageSize;
		this.endRow = startRow + pageSize;

		this.endPage = (currentPage + (blockSize - 1)) / blockSize * blockSize;
		this.startPage = endPage - (blockSize - 1);
	}

	public String getPagingHTML() {
		StringBuilder html = new StringBuilder();
		
		html.append("<ul class='pagination pagination-sm m-0 float-right'>");
		
		if(startPage > 1) {
			html.append("<li class='page-item'><a href='' class='page-link' data-page='").append(startPage - blockSize).append("'>Prev</a></li>");
		}
		
		for(int i = startPage; i <= Math.min(endPage, totalPage); i++) {
			if(i == currentPage) {
				html.append("<li class='page-item active'><span class='page-link'>").append(i).append("</span></li>");
			} else {
				html.append("<li class='page-item'><a href='' class='page-link' data-page='").append(i).append("'>").append(i).append("</a></li>");
			}
		}
		
		if(endPage < totalPage) {
			html.append("<li class='page-item'><a href='' class='page-link' data-page='").append(endPage + 1).append("'>Next</a></li>");
		}
		
		html.append("</ul>");
		
		return html.toString();
	}
}