package com.pdf.FileManager.service;

import com.pdf.FileManager.entity.ResumeEntity;
import com.pdf.FileManager.entity.User;
import com.pdf.FileManager.repository.StorageRepository;
import com.pdf.FileManager.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.pdf.FileManager.util.ImageUtils.*;

@Service
public class StorageService {

    @Autowired
    private StorageRepository repository;

    public String uploadImage(MultipartFile file, Integer userid) throws Exception {

        if(!userRepo.existsById(userid)) throw new Exception("User not found");
        User user=userRepo.findById(userid).get();

        ResumeEntity imageData = repository.save(ResumeEntity.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(file.getBytes())
                .user(user)
                .build());

            user.getImageDataList().add(imageData);
            userRepo.save(user);
        if (imageData != null) {
            return "file uploaded successfully : " + file.getOriginalFilename();
        }
        return null;
    }

    public ResponseEntity<?> downloadImage(Long id) throws Exception{
        Optional<ResumeEntity> dbImageData = repository.findById(id);
        ResumeEntity resumeEntity=dbImageData.get();
        resumeEntity.setName("resume.docx");
        byte[] images= dbImageData.get().getImageData();

       String typearray[]=dbImageData.get().getType().split("/");
//       System.out.println(typearray.length+"  "+typearray.toString());
//       if(typearray.length<=1) throw new Exception("resume with id not found");
       String type=typearray[1];

        MediaType type1=getMediaType(type);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(getMediaType(type))
                .body(images);
    }

      @Autowired
      UserRepo userRepo;

    public List<InputStream> findAllResumes(Integer userid) throws Exception
    {
        User user=userRepo.findById(userid).get();
        List<ResumeEntity> resumeEntitiesList=repository.findByUser(user);
        if(resumeEntitiesList.size()==0)
        {
            throw new Exception("Resume  Not Found Exception");
        }
         List<ResumeEntity> resumeList=new ArrayList<>();
        for(ResumeEntity resumeEntity : resumeEntitiesList)
        {
                 resumeList.add(resumeEntity);
        }



        List<InputStream> pdfStreams = new ArrayList<>();

        for (ResumeEntity pdfEntity : resumeEntitiesList) {
            // Create an InputStream from the byte array of the PDF content
            InputStream pdfStream = new ByteArrayInputStream(pdfEntity.getImageData());
            pdfStreams.add(pdfStream);
        }

        return pdfStreams;
    }

    public  String deletedResumeByIdInDb(Long rid) throws Exception
    {
        if(!repository.existsById(rid)) throw new Exception("resume with Id"+rid+"not found");
         repository.deleteById(rid);
         return "resume"+rid+" deleted successfully";
    }

    public  String deletedUserByIdInDb(Integer uid) throws Exception
    {
        if(!userRepo.existsById(uid)) throw new Exception("user with Id"+uid+"not found");

           User user=userRepo.findById(uid).get();
//           repository.deleteAll(user.getImageDataList());
          repository.deleteByUserId(uid);

        return "resume"+uid+" deleted successfully";
    }


}
