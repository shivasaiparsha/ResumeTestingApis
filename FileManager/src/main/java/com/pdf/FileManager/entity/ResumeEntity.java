package com.pdf.FileManager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "ImageData")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    @Lob
    @Column(name = "imagedata", length = 10000000)
    private byte[] imageData;

    @ManyToOne
    @JoinColumn(name = "userid")
    private User user;

     public ResumeEntity(String name, String type, byte[] imageData)
     {
         this.name=name;
         this.type=type;
         this.imageData=imageData;
     }
}