using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

using DeviceController.Configuration;
using DeviceController.Devices.DeviceA;
using DeviceController.Devices.DeviceB;

namespace DeviceController
{
    public partial class Form1 : Form
    {
        DeviceA deviceA = null;
        DeviceB deviceB = null;

        public Form1()
        {
            InitializeComponent();

        }

        private void Form1_Load(object sender, EventArgs e)
        {
            // Load the device configutation
            DeviceConfiguration.Load();

            // Initialize the devices
            deviceA = new DeviceA();
            deviceB = new DeviceB();

            textBox1.Focus();
        }      

        private void button1_Click(object sender, EventArgs e)
        {
            // Execute the command on device A and show the result in the listbox
            deviceA.DoSomething(textBox1.Text);
            listBox1.Items.Add(deviceA.Result);
            textBox1.Text = "";
            textBox1.Focus();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            // Execute the command on device B and show the result in the listbox
            deviceB.DoSomething(textBox1.Text);
            listBox1.Items.Add(deviceB.Result);
            textBox1.Text = "";
            textBox1.Focus();
        }


    }
}