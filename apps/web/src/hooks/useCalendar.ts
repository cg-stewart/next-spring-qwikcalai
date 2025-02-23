import { useMutation, useQueryClient } from '@tanstack/react-query';
import { uploadImage, processImage, ImageUpload, CalendarEvent } from '../api/calendar';

export const useCalendarUpload = () => {
  const queryClient = useQueryClient();

  const uploadMutation = useMutation({
    mutationFn: uploadImage,
    onSuccess: (data) => {
      queryClient.setQueryData(['uploads', data.id], data);
    },
  });

  const processMutation = useMutation({
    mutationFn: processImage,
    onSuccess: (data, imageId) => {
      queryClient.setQueryData(['events', imageId], data);
    },
  });

  const handleImageUpload = async (file: File) => {
    try {
      const upload = await uploadMutation.mutateAsync(file);
      const events = await processMutation.mutateAsync(upload.id);
      return { upload, events };
    } catch (error) {
      console.error('Failed to process image:', error);
      throw error;
    }
  };

  return {
    handleImageUpload,
    isUploading: uploadMutation.isPending,
    isProcessing: processMutation.isPending,
    error: uploadMutation.error || processMutation.error,
  };
};
