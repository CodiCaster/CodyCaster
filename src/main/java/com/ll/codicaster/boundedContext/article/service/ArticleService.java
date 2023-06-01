package com.ll.codicaster.boundedContext.article.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.article.form.ArticleCreateForm;
import com.ll.codicaster.boundedContext.article.repository.ArticleRepository;
import com.ll.codicaster.boundedContext.image.entity.Image;
import com.ll.codicaster.boundedContext.image.repository.ImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {

	private final ArticleRepository articleRepository;
	private final ImageRepository imageRepository;

	public void saveArticle(ArticleCreateForm form, MultipartFile imageFile) throws Exception {
		Article article = Article.builder()
			.title(form.getTitle())
			.content(form.getContent())
			.createDate(LocalDateTime.now())
			.modifyDate(LocalDateTime.now())
			.build();

		articleRepository.save(article);

		// 이미지 파일이 있으면 저장
		if (!imageFile.isEmpty()) {
			String projectPath = "C:/Users/82102/IdeaProjects/CodiCaster-main/images";
			UUID uuid = UUID.randomUUID();
			String fileName = uuid + "_" + imageFile.getOriginalFilename();

			File directory = new File(projectPath);
			// 디렉토리가 존재하지 않으면 생성
			if (!directory.exists()) {
				directory.mkdirs(); // 상위 디렉토리까지 모두 생성
			}

			File saveFile = new File(projectPath, fileName);
			imageFile.transferTo(saveFile);

			Image image = new Image();
			image.setFilename(fileName);
			image.setFilepath("/images/" + fileName);
			image.setArticle(article);  // Image 객체와 Article 객체를 연결

			image = imageRepository.save(image);  // 이미지를 DB에 저장

			article.setImage(image); // 이미지 정보를 게시글에 추가
		}

	}

	public List<Article> articleList() {

		return articleRepository.findAll();
	}

}