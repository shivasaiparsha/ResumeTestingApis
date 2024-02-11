package com.pdf.FileManager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="user")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userid;

    private String username;
    private String password;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    List<ResumeEntity>imageDataList =new ArrayList<>();

     public void addResume(ResumeEntity resumeEntity)
     {
         this.imageDataList.add(resumeEntity);
     }

     User(String username, String password)
     {
         this.username=username;
         this.password=password;

     }
}
