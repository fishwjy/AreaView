# AreaView
A image view which can set the responsible area.

## Demo
![Demo](/pic/Demo.gif)

## Usage
### 1.Add your vertex of polygon to AreaView. One by one(x and y).
        iv.addPoly(new float[]{0, 152, 125, 152, 125, 419, 265, 419, 265, 736, 405, 736, 405, 417, 538, 417, 538, 834, 0, 834},
                new AreaView.OnAreaViewClickListener() {
                    @Override
                    public void onAreaViewClick(AreaView.Shape shape) {
                        Toast.makeText(MainActivity.this, "Open Document", Toast.LENGTH_SHORT).show();
                    }
                });

        iv.addCircle(new float[]{630, 700}, 51f, new AreaView.OnAreaViewClickListener() {
            @Override
            public void onAreaViewClick(AreaView.Shape shape) {
                Toast.makeText(MainActivity.this, "Open Circle", Toast.LENGTH_SHORT).show();
            }
        });
        
### 2.Use the view like normal ImageView. If your image from internet, please use SimpleTarget to load.
        Glide.with(MainActivity.this)
                .load("http://img.blog.csdn.net/20161213103222714?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMDEyODIyMA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast")
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        iv.setImageBitmap(resource);
                    }
                });

## License
```
Copyright 2017 Vincent Woo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```