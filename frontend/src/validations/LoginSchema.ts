import * as yup from "yup";

export type LoginSchemaType = yup.InferType<typeof LoginSchema>;

export const LoginSchema = yup.object().shape({
  email: yup.string().required("email is required"),
  password: yup.string().required("Password is required"),
});
