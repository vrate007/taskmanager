package org.acme;

import jakarta.inject.Inject;
// ❌ Видалено: import jakarta.enterprise.context.ApplicationScoped; // Не потрібен тут
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskResource {

    @Inject
    TaskManager taskManager;

    // --- R (Read All & Filter) ---

    @GET
    public List<Task> getAll(
            @QueryParam("status") TaskStatus status,
            @QueryParam("priority") TaskPriority priority) {

        if (status != null) {
            return taskManager.filterTasksByStatus(status);
        }
        if (priority != null) {
            return taskManager.filterTasksByPriority(priority);
        }
        return taskManager.getAllTasks();
    }

    // 🌟 НОВИЙ ЕНДПОІНТ: Пошук за назвою
    @GET
    @Path("/search")
    public List<Task> searchByTitle(@QueryParam("title") String title) {
        if (title == null || title.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return taskManager.findTasksByTitle(title);
    }

    // 🌟 НОВИЙ ЕНДПОІНТ: Сортування
    @GET
    @Path("/sort")
    public List<Task> sortBy(@QueryParam("by") String sortBy) {

        if (sortBy == null) {
            return taskManager.getAllTasks();
        }

        return switch (sortBy.toLowerCase()) {
            case "createdat" -> taskManager.sortTasksByCreatedAt();
            case "priority" -> taskManager.sortTasksByPriority();
            case "status" -> taskManager.sortTasksByStatus();
            default -> {
                System.err.println("Невідомий параметр сортування: " + sortBy);
                yield taskManager.getAllTasks();
            }
        };
    }

    // --- R (Read By ID) ---

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") long id) {
        Optional<Task> task = taskManager.getTaskById(id);

        if (task.isPresent()) {
            return Response.ok(task.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // --- C (Create) ---

    @POST
    public Response create(TaskCreationDTO dto) {
        if (dto.title == null || dto.title.trim().isEmpty() || dto.priority == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Необхідні поля: title, priority.").build();
        }
        try {
            Task newTask = taskManager.addTask(dto.title, dto.priority);
            return Response.status(Response.Status.CREATED).entity(newTask).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // --- U (Update) ---

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") long id, TaskUpdateDTO dto) {
        Optional<Task> updatedTask = taskManager.updateTask(
                id,
                dto.title,
                dto.status,
                dto.priority
        );

        if (updatedTask.isPresent()) {
            return Response.ok(updatedTask.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // --- D (Delete) ---

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") long id) {
        // Згідно з TaskManagerInterface використовуємо removeTask
        boolean deleted = taskManager.removeTask(id);

        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // --- Внутрішні DTO (Data Transfer Objects) для вхідних даних ---

    public static class TaskCreationDTO {
        public String title;
        public TaskPriority priority;
    }

    public static class TaskUpdateDTO {
        public String title;
        public TaskStatus status;
        public TaskPriority priority;
    }
}