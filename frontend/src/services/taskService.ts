import { handleProxyRequest } from "@/app/api/proxy/[...slug]/route";

export const getTasksListService = async () => {
  const response = await handleProxyRequest(
    new Request("/api/tasks", {
      method: "GET",
    }),
    {
      params: Promise.resolve({ slug: ["tasks"] }),
    },
  );
};
