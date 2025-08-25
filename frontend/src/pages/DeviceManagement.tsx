import { useForm } from "react-hook-form"; // Ensure this is correctly imported
import { zodResolver } from "@hookform/resolvers/zod"; // Ensure this is correctly imported
import * as z from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Form, FormField, FormItem, FormLabel, FormControl, FormMessage } from "@/components/ui/form";
import { useNavigate } from "react-router-dom";

const schema = z.object({
  deviceId: z.string().nonempty("Device ID is required"),
});

export default function DeviceManagement() {
  const navigate = useNavigate();
  const form = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: { deviceId: "" },
  });

  const onSubmit = async (values: z.infer<typeof schema>) => {
    // Handle device registration logic here
    console.log(values);
    console.log(values);
    // Navigate to the dashboard after registration
    navigate("/dashboard");
  };

  return (
    <main className="flex min-h-screen items-center justify-center bg-gray-50">
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
          <FormField
            control={form.control}
            name="deviceId"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Device ID</FormLabel>
                <FormControl>
                  <Input {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <Button type="submit" className="w-full">Register Device</Button>
        </form>
      </Form>
    </main>
  );
}