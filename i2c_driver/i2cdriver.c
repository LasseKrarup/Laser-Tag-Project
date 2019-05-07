/*
    Module: i2c Driver
    Description: i2c device driver with interrupt so the RPi can act as a "slave"
    Author: Lasse Krarup
    Date: 07/05-2019
*/

#include <linux/interrupt.h>
#include <linux/gpio.h>
#include <linux/fs.h>
#include <linux/cdev.h>
#include <linux/device.h>
#include <linux/uaccess.h>
#include <linux/module.h>
#include <linux/interrupt.h>
#include <linux/wait.h>
#include <linux/sched.h>

MODULE_LICENSE("GPL");
MODULE_AUTHOR("Lasse Krarup");
MODULE_DESCRIPTION("A driver for i2c connection with the PSoC that sends a read request when receiving an interrupt. This way the RPi can be used as kind of an i2c slave.");

#define GPIO_NUM 22

static int flag = 0;
static dev_t devno;
static struct cdev character_device;
static int err;
static struct file_operations fops;
static struct class *my_class = NULL;

static irqreturn_t isr_handler(int irq, void *dev_id)
{
    printk("IRQ event\n");
    return IRQ_HANDLED;
}

/*  ====================================================
                MODULE INIT FUNCTION
    ==================================================== */
static int __init module_init(void)
{
    // Request GPIO
    err = gpio_request(SW_GPIO, "SW2");
    if (err < 0)
    {
        printk(KERN_INFO "Error requesting GPIO\n");
        goto out;
    }

    // Set GPIO Direction to input (interrupt is expected from PSoC)
    err = gpio_direction_input(GPIO_NUM);
    if (err < 0)
    {
        printk(KERN_INFO "Error setting GPIO direction\n");
        goto err_gpio_dir;
    }

    // Allocating Device Numbers
    err = alloc_chrdev_region(&devno, 0, 1, "i2c_drv_chrdev");
    if (err < 0)
    {
        printk(KERN_ALERT "Failed to allocate chrdev region\n");
        goto err_alloc_chrdev;
    }

    // Create class
    my_class = class_create(THIS_MODULE, "i2c_drv_class");
    if (my_class == NULL)
    {
        printk(KERN_ALERT "Failed to create class\n");
        goto err_class_create;
    };

    // Create device
    if (device_create(my_class, NULL, devno, NULL, "i2c_drv_dev") < 0)
    {
        printk(KERN_ALERT "Failed to create device\n");
        goto err_dev_create;
    };

    // Init cdev
    cdev_init(&character_device, &fops);

    // Add cdev
    err = cdev_add(&character_device, devno, 1); // 1 minor number
    if (err < 0)
    {
        printk(KERN_ALERT "Failed to add cdev\n");
        goto err_cdev_add;
    }

    err = request_irq(gpio_to_irq(GPIO_NUM), isr_handler, IRQF_TRIGGER_RISING, "i2c_irq", NULL);
    if (err < 0)
    {
        printk(KERN_ALERT "Failed to request IRQ\n");
        goto err_irq_request;
    }

    return 0;

// Error handling
err_irq_request:
    //free_irq(gpio_to_irq(GPIO_NUM), NULL); // Maybe not needed because it was never requested succesfully?
    cdev_del(&character_device); // Delete Cdev

err_cdev_add:
    device_destroy(my_class, devno); // Destroy device

err_dev_create:
    class_destroy(my_class); // Destroy class

err_class_create:
    unregister_chrdev_region(devno, 1); // Unregister Device (1 minor)

err_alloc_chrdev_region:
err_gpio_dir:
    gpio_free(GPIO_NUM); // Free GPIO

out:
    return err;
}

/*  ====================================================
                MODULE EXIT FUNCTION
    ==================================================== */
static void __exit module_exit(void)
{
    free_irq(gpio_to_irq(GPIO_NUM), NULL); // Free IRQ
    cdev_del(&character_device);           // Delete Cdev
    device_destroy(my_class, devno);       // Destroy device
    class_destroy(my_class);               // Destroy class
    unregister_chrdev_region(devno, 1);    // Unregister Device (1 minor)
    gpio_free(GPIO_NUM);                   // Free GPIO
}

/*  ====================================================
                FILEOPS OPEN FUNCTION
    ==================================================== */
static int open_func(struct inode *i, struct file *f)
{
    printk(KERN_INFO "Driver: open()\n");
    return 0;
}

/*  ====================================================
                FILEOPS RELEASE FUNCTION
    ==================================================== */
static int release_func(struct inode *i, struct file *f)
{
    printk(KERN_INFO "Driver: close()\n");
    return 0;
}

/*  ====================================================
                FILEOPS READ FUNCTION
    ==================================================== */
static ssize_t read_func(struct file *f, char __user *buf, size_t len, loff_t *off)
{
    printk(KERN_INFO "Driver: read()\n");
    return 0;
}

/*  ====================================================
                FILEOPS WRITE FUNCTION
    ==================================================== */
static ssize_t write_func(struct file *f, const char __user *buf,
                          size_t len, loff_t *off)
{
    printk(KERN_INFO "Driver: write()\n");
    return len;
}

static struct file_operations my_fops = {
    .owner = THIS_MODULE,
    .open = open_func,
    .release = release_func,
    .read = read_func,
    .write = write_func,
};

module_init(module_init);
module_exit(module_exit);