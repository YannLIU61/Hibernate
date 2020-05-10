package com.yann.hibernate.helloworld;

import java.sql.Blob;
import java.util.Date;

public class News {
	private Integer id;
	private String author;
	private String title;
	private Date date;
	private String content;
	private Blob image;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Blob getImage() {
		return image;
	}

	public void setImage(Blob image) {
		this.image = image;
	}

	// 必须提供无参构造器
	public News() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public News(String author, String title, Date date) {
		this.author = author;
		this.title = title;
		this.date = date;
	}

	@Override
	public String toString() {
		return "News [id=" + id + ", author=" + author + ", title=" + title + ", date=" + date + "]";
	}

}
