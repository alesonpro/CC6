package com.example.cp_01

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class MainActivity : AppCompatActivity() {
    private lateinit var renderable: ModelRenderable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val renderableSource = RenderableSource.builder()
            .setSource(
                this,
                Uri.parse("house.glb"),
                RenderableSource.SourceType.GLB
            )
            .setRecenterMode(RenderableSource.RecenterMode.ROOT)
            .build()

        ModelRenderable.builder()
            .setSource(this, renderableSource)
            .build()
            .thenAccept { modelRenderable ->
                renderable = modelRenderable
            }
            .exceptionally { throwable ->
                Toast.makeText(
                    this,
                    "Unable to load model: ${throwable.message}",
                    Toast.LENGTH_SHORT
                ).show()
                null
            }

        val fragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as? ArFragment

        fragment?.setOnTapArPlaneListener { hitResult, _, _ ->
            val anchorNode = AnchorNode(hitResult.createAnchor())
            anchorNode.setParent(fragment.arSceneView.scene)

            val transformableNode = TransformableNode(fragment.transformationSystem)
            transformableNode.renderable = renderable
            transformableNode.setParent(anchorNode)
            transformableNode.select()
        } ?: run {
            Toast.makeText(this, "AR Fragment not found", Toast.LENGTH_SHORT).show()
        }
    }
}
