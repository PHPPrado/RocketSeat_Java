package com.rocketseat.todolist.task;

import com.rocketseat.todolist.utils.Utils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpClient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody Task task, HttpServletRequest request){
        System.out.println("Chegou no controller");
        var idUser = request.getAttribute("idUser");
        task.setIdUser((UUID) idUser);

        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(task.getStartAt()) || task.getStartAt().isAfter(task.getEndAt()) ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data inválida");
        }

        var taskToReturn = this.taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/")
    public List<Task> list(HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        var tasks = this.taskRepository.findByIdUser((UUID) idUser);
        return tasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody Task task, @PathVariable UUID id, HttpServletRequest request){
        var taskLocal = this.taskRepository.findById(id).orElse(null);

        if(task == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada");
        }

        var idUser = request.getAttribute("idUser");

        if(!task.getIdUser().equals(idUser)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário sem permissão");
        }

        Utils.copyNonNullProperties(task, taskLocal);
        var taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.ok().body(this.taskRepository.save(taskUpdated));

    }


}
