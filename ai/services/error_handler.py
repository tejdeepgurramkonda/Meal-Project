# Error handling utilities
import traceback


def safe_execute(func):
    async def wrapper(*args, **kwargs):
        try:
            return await func(*args, **kwargs)
        except Exception as e:
            return {
                "status": "error",
                "message": "AI service failed safely",
                "details": str(e),
                "trace": traceback.format_exc()
            }
    return wrapper