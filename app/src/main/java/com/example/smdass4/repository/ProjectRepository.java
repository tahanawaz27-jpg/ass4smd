package com.example.smdass4.repository;

import com.example.smdass4.model.Attachment;
import com.example.smdass4.model.Project;
import com.example.smdass4.model.Task;

import java.util.ArrayList;
import java.util.List;

public class ProjectRepository {
    private static ProjectRepository instance;
    private final List<Project> projects;

    private ProjectRepository() {
        projects = new ArrayList<>();
        initDummyData();
    }

    public static synchronized ProjectRepository getInstance() {
        if (instance == null) {
            instance = new ProjectRepository();
        }
        return instance;
    }

    private void initDummyData() {
        // Project 1: Service Page Website
        List<Task> tasks1 = new ArrayList<>();
        tasks1.add(new Task("t1", "Create low fidelity service page website", "Overdue", "Normal", "15 May 2022", false));
        tasks1.add(new Task("t2", "Configure pricing table responsive grid", "In Progress", "Normal", "22 May 2022", false));
        tasks1.add(new Task("t3", "Add form validation", "To Do", "High", "Due today", false));
        tasks1.add(new Task("t4", "Create brand guidelines for website design", "Done", "Normal", "18 May 2022", true));
        tasks1.add(new Task("t5", "Draft hero copy and CTA text", "Done", "Low", "16 May 2022", true));
        tasks1.add(new Task("t6", "Setup SEO meta tags and open graph info", "To Do", "Normal", "25 May 2022", false));
        tasks1.add(new Task("t7", "Optimize hero images and assets", "To Do", "Normal", "26 May 2022", false));

        List<Attachment> attachments1 = new ArrayList<>();
        attachments1.add(new Attachment("a1", "sports-projectv2.fig", "Image", "2.4 MB", "Link", "Figma Design Mockups for Service Page Website v2"));
        attachments1.add(new Attachment("a2", "briefing from client.pdf", "Document", "550 KB", "Link", "Client Requirements & Project Scope Document PDF"));
        attachments1.add(new Attachment("a3", "mount asset.png", "Image", "1.1 MB", "Link", "Hero Asset Graphic Mountain Background PNG"));

        Project p1 = new Project(
                "p1",
                "User Interface",
                "Service Page Website",
                "Make a page display about services for company website with blue and red colors and provide some price information on each product card.",
                3,
                tasks1,
                attachments1
        );

        // Project 2: Login API
        List<Task> tasks2 = new ArrayList<>();
        tasks2.add(new Task("t21", "Build authentication endpoints and JWT token refresh handlers", "In Progress", "High", "20 May 2022", false));
        tasks2.add(new Task("t22", "Implement password reset flow", "To Do", "Normal", "24 May 2022", false));
        tasks2.add(new Task("t23", "Write API unit tests", "Done", "High", "12 May 2022", true));

        List<Attachment> attachments2 = new ArrayList<>();
        attachments2.add(new Attachment("a21", "api_spec.json", "Document", "120 KB", "Link", "OpenAPI spec definition"));
        attachments2.add(new Attachment("a22", "db_schema.png", "Image", "450 KB", "Link", "Database ER diagram"));

        Project p2 = new Project(
                "p2",
                "Backend",
                "Login API",
                "Build authentication endpoints and JWT token refresh handlers for mobile and web clients.",
                3,
                tasks2,
                attachments2
        );

        // Project 3: Mobile Dashboard
        List<Task> tasks3 = new ArrayList<>();
        tasks3.add(new Task("t31", "Responsive overview widgets and telemetry cards layout design", "In Progress", "Normal", "30 May 2022", false));
        tasks3.add(new Task("t32", "Connect WebSocket live feed", "To Do", "High", "02 Jun 2022", false));
        tasks3.add(new Task("t33", "Chart component integration", "To Do", "Normal", "05 Jun 2022", false));
        tasks3.add(new Task("t34", "Setup dark theme colors", "Done", "Low", "10 May 2022", true));

        List<Attachment> attachments3 = new ArrayList<>();
        attachments3.add(new Attachment("a31", "dashboard_wireframe.png", "Image", "1.8 MB", "Link", "Mobile Dashboard Layout Wireframe"));

        Project p3 = new Project(
                "p3",
                "Frontend",
                "Mobile Dashboard",
                "Responsive overview widgets and telemetry cards for mobile display.",
                1,
                tasks3,
                attachments3
        );

        // Project 4: Release Checklist
        List<Task> tasks4 = new ArrayList<>();
        tasks4.add(new Task("t41", "Pre-production verification and smoke tests", "Done", "High", "14 May 2022", true));
        tasks4.add(new Task("t42", "Security audit check", "Done", "High", "15 May 2022", true));

        List<Attachment> attachments4 = new ArrayList<>();
        attachments4.add(new Attachment("a41", "release_notes.txt", "Text", "15 KB", "Link", "Release Notes v1.0.0"));

        Project p4 = new Project(
                "p4",
                "QA & Ops",
                "Release Checklist",
                "Pre-production verification and smoke tests before deployment.",
                2,
                tasks4,
                attachments4
        );

        // Project 5: Design Assets
        List<Task> tasks5 = new ArrayList<>();
        tasks5.add(new Task("t51", "Vector icons and illustration library for landing pages", "To Do", "Normal", "10 Jun 2022", false));

        List<Attachment> attachments5 = new ArrayList<>();

        Project p5 = new Project(
                "p5",
                "Design",
                "Design Assets",
                "Vector icons and illustration library for landing pages.",
                0,
                tasks5,
                attachments5
        );

        projects.add(p1);
        projects.add(p2);
        projects.add(p3);
        projects.add(p4);
        projects.add(p5);
    }

    public List<Project> getProjects() {
        List<Project> copies = new ArrayList<>();
        for (Project p : projects) {
            copies.add(p.copy());
        }
        return copies;
    }

    public Project getProjectById(String id) {
        for (Project p : projects) {
            if (p.getId().equals(id)) {
                return p.copy();
            }
        }
        return null;
    }

    public void updateProject(Project updatedProject) {
        if (updatedProject == null || updatedProject.getId() == null) return;
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getId().equals(updatedProject.getId())) {
                projects.set(i, updatedProject.copy());
                return;
            }
        }
    }

    public void addProject(Project newProject) {
        if (newProject != null) {
            projects.add(0, newProject.copy());
        }
    }
}