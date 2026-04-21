import { getTasksListService } from "@/services/taskService";

const TasksPage = async () => {
  const tasks = await getTasksListService({
    page: 0,
    size: 10,
  });
  console.log("Tasks:", tasks);
  return (
    <div>
      <h1>Tasks Page</h1>
    </div>
  );
};

export default TasksPage;
