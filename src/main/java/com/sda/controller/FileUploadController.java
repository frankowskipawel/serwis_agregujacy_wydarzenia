package com.sda.controller;


import com.sda.model.Event;
import com.sda.service.EventService;
import com.sda.storage.StorageFileNotFoundException;
import com.sda.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    EventService eventService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

//    @Autowired
//    Cart cart;

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/event/eventAddPhoto")
    public String handleFileUpload(Model model, @RequestParam("file") MultipartFile file,
                                   @RequestParam(value = "event", required = false) Event event,
                                   RedirectAttributes redirectAttributes) throws IOException {

        System.out.println("+++++" + event);
        storageService.store(file);

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(com.sda.controller.FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        return "redirect:/event/addEvent?picture=" + storageService.getNameLastStorageFile();
    }

//    @PostMapping("/admin/productEditfile")
//    public String handleFileUploadEditProduct(Model model, @RequestParam("file") MultipartFile file,
//                                              @ModelAttribute(value = "product") Event event,
//                                              RedirectAttributes redirectAttributes) throws IOException {
//
//        System.out.println("+++++" + event);
//        storageService.store(file);
//
//        redirectAttributes.addFlashAttribute("message",
//                "You successfully uploaded " + file.getOriginalFilename() + "!");
//
//        model.addAttribute("files", storageService.loadAll().map(
//                path -> MvcUriComponentsBuilder.fromMethodName(com.sda.controller.FileUploadController.class,
//                        "serveFile", path.getFileName().toString()).build().toUri().toString())
//                .collect(Collectors.toList()));
//
//        model.addAttribute("product", product);
//        model.addAttribute("picture", storageService.getNameLastStorageFile());
//
//        return "redirect:/admin/productEdit?picture=" + storageService.getNameLastStorageFile()+
//                "&productId="+product.getId();
//    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}