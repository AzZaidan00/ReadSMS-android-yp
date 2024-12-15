package com.azim.readsms

import android.app.ListActivity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.cursoradapter.widget.CursorAdapter
import com.azim.readsms.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

// for best practice use recyclerview
// List Activitu untuk tunjukkan SMSM di dalam SMS
class MainActivity : ListActivity() {
    private lateinit var binding: ActivityMainBinding

    val SMS = Uri.parse("content://sms")
    val PERMISSIONS_REQUEST_READ_SMS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding  = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val permissionCheck = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.READ_SMS)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            readSMS()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_SMS),
                PERMISSIONS_REQUEST_READ_SMS)
        }
    }

    // Column di dalam table SMS yang akan diakses melalui content provider
    object SmsColumns {
        val ID = "_id"
        val ADDRESS = "address"
        val DATE = "date"
        val BODY = "body"
    }

    private inner class SmsCursorAdapter(context: Context, c: Cursor, autorequery:Boolean):
            CursorAdapter(context,c,autorequery) {

        override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
            return View.inflate(context,R.layout.custom_row,null)
        }

        override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
            view!!.findViewById<TextView>(R.id.smsOrigin).text = cursor!!.getString(
                cursor.getColumnIndexOrThrow(SmsColumns.ADDRESS))
            view!!.findViewById<TextView>(R.id.smsBody).text = cursor!!.getString(
                cursor.getColumnIndexOrThrow(SmsColumns.BODY))
            view!!.findViewById<TextView>(R.id.smsDate).text = cursor!!.getString(
                cursor.getColumnIndexOrThrow(SmsColumns.DATE))
        }
            }

    private fun readSMS(){
        // Baca database dari Content Provider
        // Retrieve the SMS DB, Specify
        val cursor = contentResolver.query(SMS, arrayOf(SmsColumns.ID,
            SmsColumns.BODY,SmsColumns.DATE,SmsColumns.ADDRESS),null,null,
            SmsColumns.DATE + " DESC")
        val adapter = SmsCursorAdapter(this,cursor!!,true)
        listAdapter = adapter
    }

    // Function yang dipanggil
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            PERMISSIONS_REQUEST_READ_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Jika permission diberi
                    readSMS()
                } else {
                    // Jika permission tidak diberi
                    Snackbar.make(binding.root,"Permission not granted",
                        Snackbar.LENGTH_LONG).show()
                }
                return
            }
        }
    }
}