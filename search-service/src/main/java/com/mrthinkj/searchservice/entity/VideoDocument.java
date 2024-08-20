package com.mrthinkj.searchservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

@Document(indexName = "videos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VideoDocument {
    @Id
    private Long id;
    @Field(type = FieldType.Text)
    private String videoUUID;
    @Field(type = FieldType.Text)
    private String thumbnailUUID;
    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;
    @Field(type = FieldType.Date)
    private LocalDate publishDate;
    private Long posterId;
}
