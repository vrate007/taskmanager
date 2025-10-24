package org.acme.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.service.PersistenceManager;
import org.acme.domain.Task;
import org.acme.domain.TaskPriority;
import org.acme.domain.TaskStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

// Базовий шлях для всіх операцій із задачами
@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskResource {

    // Інжектуємо PersistenceManager, який тепер керує бізнес-логікою та персистентністю
    @Inject
    PersistenceManager persistenceManager;

    // --- R (Read All & Filter) ---
    // GET /tasks?status=NEW&priority=HIGH
    @GET
    public List<Task> getAll(
            @QueryParam("status") TaskStatus status,
            @QueryParam("priority") TaskPriority priority) {

        if (status != null) {
            return persistenceManager.filterTasksByStatus(status);
        }
        if (priority != null) {
            return persistenceManager.filterTasksByPriority(priority);
        }
        return persistenceManager.getAllTasks();
    }

    // --- R (Search By Title) ---
    // GET /tasks/search?title=buy
    @GET
    @Path("/search")
    public List<Task> searchByTitle(@QueryParam("title") String title) {
        if (title == null || title.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return persistenceManager.findTasksByTitle(title);
    }

    // --- R (Sort) ---
    // GET /tasks/sort?by=priority
    @GET
    @Path("/sort")
    public List<Task> sortBy(@QueryParam("by") String sortBy) {

        if (sortBy == null) {
            return persistenceManager.getAllTasks();
        }

        return switch (sortBy.toLowerCase()) {
            case "createdat" -> persistenceManager.sortTasksByCreatedAt();
            case "priority" -> persistenceManager.sortTasksByPriority();
            case "status" -> persistenceManager.sortTasksByStatus();
            default -> {
                System.err.println("Невідомий параметр сортування: " + sortBy);
                yield persistenceManager.getAllTasks();
            }
        };
    }

    // --- R (Read By ID) ---
    // GET /tasks/{id}
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") long id) {
        Optional<Task> task = persistenceManager.getTaskById(id);

        if (task.isPresent()) {
            return Response.ok(task.get()).build(); // 200 OK
        } else {
            return Response.status(Response.Status.NOT_FOUND).build(); // 404 Not Found
        }
    }

    // --- C (Create) ---
    // POST /tasks
    @POST
    public Response create(TaskCreationDTO dto) {
        if (dto.title == null || dto.title.trim().isEmpty() || dto.priority == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Необхідні поля: title, priority.").build();
        }
        try {
            Task newTask = persistenceManager.addTask(dto.title, dto.priority);
            return Response.status(Response.Status.CREATED).entity(newTask).build(); // 201 Created
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // --- U (Update) ---
    // PUT /tasks/{id}
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") long id, TaskUpdateDTO dto) {
        Optional<Task> updatedTask = persistenceManager.updateTask(
                id,
                dto.title,
                dto.status,
                dto.priority
        );

        if (updatedTask.isPresent()) {
            return Response.ok(updatedTask.get()).build(); // 200 OK
        } else {
            return Response.status(Response.Status.NOT_FOUND).build(); // 404 Not Found
        }
    }

    // --- D (Delete) ---
    // DELETE /tasks/{id}
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") long id) {
        boolean deleted = persistenceManager.removeTask(id);

        if (deleted) {
            return Response.noContent().build(); // 204 No Content
        } else {
            return Response.status(Response.Status.NOT_FOUND).build(); // 404 Not Found
        }
    }

    // --- Внутрішні DTO (Data Transfer Objects) для вхідних даних ---

    // DTO для POST-запиту
    public static class TaskCreationDTO {
        public String title;
        public TaskPriority priority;
    }

    // DTO для PUT-запиту
    public static class TaskUpdateDTO {
        public String title;
        public TaskStatus status;
        public TaskPriority priority;
    }
}
