package org.jab.docsearch;

public class InitAdapterMeta {
	
	private String title;
	private String content;
	private String date;
	private String size;
	private String author;
	private String percent;
	private String path;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getPercent() {
		return percent;
	}
	public void setPercent(String percent) {
		this.percent = percent;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	public InitAdapterMeta() {
		super();
	}
	public InitAdapterMeta(String title, String content, String date,
			String size, String author, String percent, String path) {
		super();
		this.title = title;
		this.content = content;
		this.date = date;
		this.size = size;
		this.author = author;
		this.percent = percent;
		this.path = path;
	}
	
	@Override
	public String toString() {
		return "InitAdapterMeta [title=" + title + ", content=" + content
				+ ", date=" + date + ", size=" + size + ", author=" + author
				+ ", percent=" + percent + ", path=" + path + "]";
	}
	
}
